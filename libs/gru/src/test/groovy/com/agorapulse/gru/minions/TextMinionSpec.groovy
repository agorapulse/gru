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
package com.agorapulse.gru.minions

import com.agorapulse.gru.Client
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.TestClient
import com.agorapulse.gru.content.FileContent
import spock.lang.Specification
import spock.lang.TempDir

import static com.agorapulse.gru.minions.AbstractContentMinion.TEST_RESOURCES_FOLDER_PROPERTY_NAME

/**
 * Tests for TextMinion.
 */
class TextMinionSpec extends Specification {

    private static final String TEXT = 'Hello World'
    private static final String FILE_NAME = 'hello.txt'
    private static final String EXPECTED_FILE_LOCATION = 'com/agorapulse/gru/minions/TextMinionSpec/' + FILE_NAME

    @TempDir File tmp

    @SuppressWarnings('UnnecessaryGetter')
    void 'fixture file is created'() {
        given:
            File testResourcesFolder = tmp
            System.setProperty(TEST_RESOURCES_FOLDER_PROPERTY_NAME, testResourcesFolder.canonicalPath)
            TextMinion textMinion = new TextMinion(responseContent: FileContent.create(FILE_NAME))
            Client.Response response = Mock(Client.Response) {
                getText() >> TEXT
            }
            Client client = new TestClient(this, null, response)
            Squad squad = new Squad()
        expect:
            client.unitTest == this
        when:
            textMinion.verify(client, squad, GruContext.EMPTY)
        then:
            AssertionError error = thrown(AssertionError)
            error.message
            error.message.startsWith('Fixture files were created or updated:')
        when:
            File fixtureFile = new File(testResourcesFolder, EXPECTED_FILE_LOCATION)
        then:
            fixtureFile.exists()
            fixtureFile.text == TEXT
    }

}
