/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2022 Agorapulse.
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
package com.agorapulse.gru.minions;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.Content;
import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;
import com.agorapulse.gru.jsonunit.MatchesIsoDateNow;
import com.agorapulse.gru.jsonunit.MatchesPattern;
import com.agorapulse.gru.jsonunit.MatchesUrl;
import com.agorapulse.gru.content.ContentUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Minion responsible for JSON requests and responses.
 */
public class JsonMinion extends AbstractContentMinion<Client> {

    private Content requestJsonContent;
    private Function<JsonFluentAssert.ConfigurableJsonFluentAssert, JsonFluentAssert.ConfigurableJsonFluentAssert> jsonUnitConfiguration = jsonFluentAssert -> jsonFluentAssert;

    public JsonMinion() {
        super(Client.class);
    }

    @Override
    public GruContext doBeforeRun(Client client, Squad squad, GruContext context) {
        if (requestJsonContent != null) {
            final InputStream is = requestJsonContent.load(client);
            String requestText = (is == null ? null : ContentUtils.getText(is));
            if (requestText == null && requestJsonContent.isSaveSupported()) {
                try {
                    requestJsonContent.save(client, new ByteArrayInputStream("{\n\n}".getBytes()));
                    System.err.println("Content missing for " + requestJsonContent + ". Content was saved.");
                    requestText = "{}";
                } catch (IOException e) {
                    throw new IllegalArgumentException("Cannot save content " + requestJsonContent);
                }
            }

            client.getRequest().setJson(requestText);
        }

        return context;
    }

    public int getIndex() {
        return JSON_MINION_INDEX;
    }

    public void setRequestJsonContent(Content requestJsonContent) {
        this.requestJsonContent = requestJsonContent;
    }

    public void setJsonUnitConfiguration(Function<JsonFluentAssert.ConfigurableJsonFluentAssert, JsonFluentAssert.ConfigurableJsonFluentAssert> jsonUnitConfiguration) {
        this.jsonUnitConfiguration = jsonUnitConfiguration;
    }

    protected void similar(String actual, String expected) throws AssertionError {
        JsonFluentAssert.ConfigurableJsonFluentAssert fluentAssert = JsonFluentAssert
            .assertThatJson(actual)
            .as("Response must match " + getResponseContent() + " content")
            .withMatcher("isoDate", MatchesPattern.ISO_DATE)
            .withMatcher("isoDateNow", MatchesIsoDateNow.INSTANCE)
            .withMatcher("positiveIntegerString", MatchesPattern.POSITIVE_NUMBER_STRING)
            .withMatcher("url", MatchesUrl.INSTANCE);

        fluentAssert = jsonUnitConfiguration.apply(fluentAssert);

        try {
            fluentAssert.isEqualTo(expected);
        } catch (AssertionError error) {
            System.err.println("ACTUAL:");
            System.err.println(actual);
            System.err.println("EXPECTED:");
            System.err.println(expected);
            throw error;
        }

    }

    @Override
    protected boolean isRewriteSupported() {
        return true;
    }

    @Override
    protected String rewrite(Client client, String actualText, String expectedText) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object actual = mapper.readValue(actualText, Object.class);
            Object expected = mapper.readValue(expectedText, Object.class);

            Object merged = mergeObjects(actual, expected);

            String mergedJson = mapper.writeValueAsString(merged);

            if (getResponseContent().isSaveSupported()) {
                getResponseContent().save(client, new ByteArrayInputStream(normalize(mergedJson).getBytes()));
                System.err.println("Content rewritten for " + getResponseContent() + ". File was updated with content:\n" + mergedJson);
                createdResources.add(getResponseContent());
            }

            return mergedJson;
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot rewrite JSON file", e);
        }
    }


    @SuppressWarnings("rawtypes")
    private static Object mergeObjects(Object actual, Object expected) {
        if (expected instanceof String && ((String) expected).startsWith("${json-unit.")) {
            // keep the placeholders
            return expected;
        }


        if (actual == null) {
            return null;
        }


        if (expected == null) {
            return actual;
        }


        if (actual instanceof Map && expected instanceof Map) {
            Map actualMap = (Map) actual;
            Map expectedMap = (Map) expected;
            return mergeMaps(actualMap, expectedMap);
        }


        if (actual instanceof List && expected instanceof List) {
            List actualList = (List) actual;
            List expectedList = (List) expected;
            return mergeLists(actualList, expectedList);
        }


        if (actual instanceof Map && expected instanceof List) {
            Map actualMap = (Map) actual;
            List expectedList = (List) expected;
            return mergeListToMap(actualMap, expectedList);
        }


        if (actual instanceof List && expected instanceof Map) {
            List actualList = (List) actual;
            Map expectedMap = (Map) expected;
            return mergeMapToList(actualList, expectedMap);
        }


        // primitive value and the expected value was not placeholder
        return actual;
    }

    @SuppressWarnings("rawtypes")
    private static List mergeMapToList(final List actual, final Map expected) {
        // expect map is not single item of the list
        return mergeLists(actual, new ArrayList<>(Collections.singletonList(expected)));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Map mergeListToMap(final Map actual, final List expected) {
        // expect list has moved to first entry of the map
        Map map = new LinkedHashMap<>(1);
        map.put(actual.keySet().iterator().next(), expected);
        return mergeMaps(actual, map);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static List mergeLists(final List actual, final List expected) {
        final List result = new ArrayList();
        for (int i = 0; i < actual.size(); i++) {
            Object entry = actual.get(i);
            if (i < expected.size()) {
                result.add(mergeObjects(entry, expected.get(i)));
            } else {
                result.add(entry);
            }
        }
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Map mergeMaps(final Map actual, final Map expected) {
        Set removedKeys = new LinkedHashSet<>(expected.keySet());
        removedKeys.removeAll(actual.keySet());

        removedKeys.forEach(expected::remove);

        for (Object key : actual.keySet()) {
            expected.put(key, mergeObjects(actual.get(key), expected.get(key)));
        }

        return expected;
    }

}
