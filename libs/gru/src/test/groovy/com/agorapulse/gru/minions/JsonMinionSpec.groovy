/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2024 Agorapulse.
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
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.TestClient
import com.agorapulse.gru.content.FileContent
import spock.lang.Specification
import spock.lang.TempDir
import spock.util.environment.RestoreSystemProperties

import static com.agorapulse.gru.minions.AbstractContentMinion.SYSTEM_PROPERTY_REWRITE_RESOURCES
import static com.agorapulse.gru.minions.AbstractContentMinion.TEST_RESOURCES_FOLDER_PROPERTY_NAME

/**
 * Tests for TextMinion.
 */
@RestoreSystemProperties
class JsonMinionSpec extends Specification {

    private static final String JSON = '{\n\n}'
    private static final String FILE_NAME = 'empty.json'
    private static final String FILE_NAME_EXPECTED = 'expected.json'
    private static final String FILE_NAME_ACTUAL = 'actual.json'
    private static final String FILE_NAME_AFTER_REWRITE = 'rewritten.json'
    private static final String FIXTURES_ROOT = 'com/agorapulse/gru/minions/JsonMinionSpec/'

    @TempDir File tmp

    void 'request fixture file is created'() {
        given:
            File testResourcesFolder = tmp
            System.setProperty(TEST_RESOURCES_FOLDER_PROPERTY_NAME, testResourcesFolder.canonicalPath)
            JsonMinion jsonMinion = new JsonMinion(requestJsonContent: FileContent.create(FILE_NAME))
            Client client = new TestClient(this, Mock(Client.Request), Mock(Client.Response))
            Squad squad = new Squad()
        when:
            jsonMinion.beforeRun(client, squad, GruContext.EMPTY)
            File fixtureFile = new File(testResourcesFolder, FIXTURES_ROOT + FILE_NAME)
        then:
            fixtureFile.exists()
            fixtureFile.text == JSON
    }

    void 'response file is rewritten'() {
        given:
            File testResourcesFolder = tmp

            System.setProperty(TEST_RESOURCES_FOLDER_PROPERTY_NAME, testResourcesFolder.canonicalPath)
            System.setProperty(SYSTEM_PROPERTY_REWRITE_RESOURCES, true.toString())

            new File(testResourcesFolder, FILE_NAME_EXPECTED) << load(FILE_NAME_EXPECTED)
            JsonMinion jsonMinion = new JsonMinion(responseContent: FileContent.create(FILE_NAME_EXPECTED))

            Client.Request req = Mock(Client.Request)
            Client.Response resp = Mock(Client.Response)
            Client client = new TestClient(this, req, resp)
            Squad squad = new Squad()
        when:
            jsonMinion.verify(client, squad, GruContext.EMPTY)
        then:
            thrown(AssertionError)

            1 * resp.text >> load(FILE_NAME_ACTUAL)
        when:
            File fixtureFile = new File(testResourcesFolder, FIXTURES_ROOT + FILE_NAME_EXPECTED)
        then:
            fixtureFile.exists()

            jsonMinion.similar(
                escapeJsonUnitIgnore(fixtureFile.text),
                escapeJsonUnitIgnore(load(FILE_NAME_AFTER_REWRITE))
            )
    }

    private static String load(String name) {
        JsonMinionSpec.getResourceAsStream(JsonMinionSpec.simpleName + '/' + name).text
    }

    private static String escapeJsonUnitIgnore(String original) {
        original.replaceAll(/\$\{json-unit/, '_json_unit')
    }

}
