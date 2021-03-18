/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2021 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agorapulse.gru.minions

import com.agorapulse.gru.Client
import com.agorapulse.gru.Content
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.jsonunit.MatchesIsoDateNow
import com.agorapulse.gru.jsonunit.MatchesPattern
import com.agorapulse.gru.jsonunit.MatchesUrl
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.util.logging.Log
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert

import java.util.function.Function

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson

/**
 * Minion responsible for JSON requests and responses.
 */
@Log @CompileStatic
class JsonMinion extends AbstractContentMinion<Client> {

    final int index = JSON_MINION_INDEX

    Content requestJsonContent

    @SuppressWarnings('unchecked')
    Function<JsonFluentAssert.ConfigurableJsonFluentAssert, JsonFluentAssert.ConfigurableJsonFluentAssert> jsonUnitConfiguration =
        new Function<JsonFluentAssert.ConfigurableJsonFluentAssert, JsonFluentAssert.ConfigurableJsonFluentAssert>() {

            @Override
            JsonFluentAssert.ConfigurableJsonFluentAssert apply(JsonFluentAssert.ConfigurableJsonFluentAssert jsonFluentAssert) {
                return jsonFluentAssert
            }

        }

    JsonMinion() {
        super(Client)
    }

    @Override
    GruContext doBeforeRun(Client client, Squad squad, GruContext context) {
        if (requestJsonContent) {
            String requestText = requestJsonContent?.load(client)?.text
            if (!requestText && requestJsonContent.saveSupported) {
                requestJsonContent.save(client, new ByteArrayInputStream('{\n\n}'.bytes))
                log.warning("Content missing for $requestJsonContent. Content was saved.")
                requestText = '{}'
            }
            client.request.json = requestText
        }
        context
    }

    List mergeMapToList(List actual, Map expected) {
        // expect map is not single item of the list
        mergeLists(actual, [expected])
    }

    Map mergeListToMap(Map actual, List expected) {
        // expect list has moved to first entry of the map
        mergeMaps(actual, [(actual.keySet().first()): expected])
    }

    List mergeLists(List actual, List expected) {
        List result = []
        actual.eachWithIndex { Object entry, int i ->
            if (i < expected.size()) {
                result.add(mergeObjects(entry, expected[i]))
            } else {
                result.add(entry)
            }
        }
        return result
    }

    Map mergeMaps(Map actual, Map expected) {
        Set removedKeys = expected.keySet() - actual.keySet()

        removedKeys.each expected.&remove

        actual.keySet().each {
            expected.put(it, mergeObjects(actual[it], expected[it]))
        }

        return expected
    }

    protected void similar(String actual, String expected) throws AssertionError {
        JsonFluentAssert fluentAssert = assertThatJson(actual)
                .as("Response must match ${responseContent} content")
                .withMatcher('isoDate', MatchesPattern.ISO_DATE)
                .withMatcher('isoDateNow', MatchesIsoDateNow.INSTANCE)
                .withMatcher('positiveIntegerString', MatchesPattern.POSITIVE_NUMBER_STRING)
                .withMatcher('url', MatchesUrl.INSTANCE)

        fluentAssert = jsonUnitConfiguration.apply(fluentAssert)

        try {
            fluentAssert.isEqualTo(expected)
        } catch (AssertionError error) {
            log.info 'ACTUAL:'
            log.info actual
            log.info 'EXPECTED:'
            log.info expected
            throw error
        }
    }

    @Override
    protected boolean isRewriteSupported() {
        return true
    }

    @Override
    protected String rewrite(Client client, String actualText, String expectedText) {
        JsonSlurper slurper = new JsonSlurper()
        Object actual = slurper.parseText(actualText)
        Object expected = slurper.parseText(expectedText)

        Object merged = mergeObjects(actual, expected)

        String mergedJson = JsonOutput.toJson(merged)

        if (responseContent.saveSupported) {
            responseContent.save(client, new ByteArrayInputStream(normalize(mergedJson).bytes))
            log.warning("Content rewritten for $responseContent. File was updated with content:\n$mergedJson")
            createdResources << responseContent.toString()
        }

        mergedJson
    }

    @SuppressWarnings('Instanceof')
    private Object mergeObjects(Object actual, Object expected) {
        if (expected instanceof String && expected.startsWith('${json-unit.')) {
            // keep the placeholders
            return expected
        }

        if (actual == null) {
            return null
        }

        if (expected == null) {
            return actual
        }

        if (actual instanceof Map && expected instanceof Map) {
            Map actualMap = actual as Map
            Map expectedMap = expected as Map
            return mergeMaps(actualMap, expectedMap)
        }

        if (actual instanceof List && expected instanceof List) {
            List actualList = actual as List
            List expectedList = expected as List
            return mergeLists(actualList, expectedList)
        }

        if (actual instanceof Map && expected instanceof List) {
            Map actualMap = actual as Map
            List expectedList = expected as List
            return mergeListToMap(actualMap, expectedList)
        }

        if (actual instanceof List && expected instanceof Map) {
            List actualList = actual as List
            Map expectedMap = expected as Map
            return mergeMapToList(actualList, expectedMap)
        }

        // primitive value and the expected value was not placeholder
        return actual
    }
}
