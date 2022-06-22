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
import com.agorapulse.gru.Gru
import com.agorapulse.gru.Squad
import com.agorapulse.gru.TestClient
import org.hamcrest.Matchers
import spock.lang.Specification

import static com.agorapulse.gru.HttpStatusShortcuts.OK

/**
 * Tests for http minion.
 */
class HttpMinionSpec extends Specification {

    void 'multi header response is matched'() {
        given:
        Client.Request request = Mock(Client.Request)
        Client.Response response = Mock(Client.Response)

        Client client = new TestClient(this, request, response)
        Squad squad = new Squad()
        HttpMinion minion = new HttpMinion()
        squad.add(minion)

        String expectedHeader = "X-Foo"
        response.getHeaders(expectedHeader) >> ["Bar", "Baz"]
        response.getStatus() >> OK

        when:
        Gru gru = Gru.create(client)

        gru.verify {
            get '/foo/bar'
            expect {
                status(OK)
                header(expectedHeader, Matchers.equalToIgnoringCase("Bar"))
                header(expectedHeader, Matchers.equalToIgnoringCase("Baz"))
            }
        }
        then:
        noExceptionThrown()
    }

    void 'legacy header matching still works'() {
        given:
        Client.Request request = Mock(Client.Request)
        Client.Response response = Mock(Client.Response)

        Client client = new TestClient(this, request, response)
        Squad squad = new Squad()
        HttpMinion minion = new HttpMinion()
        squad.add(minion)

        String expectedHeader = "X-Foo"
        response.getHeaders(expectedHeader) >> ["Bar", "Baz"]
        response.getStatus() >> OK

        when:
        Gru gru = Gru.create(client)

        gru.verify {
            get '/foo/bar'
            expect {
                status(OK)
                header(expectedHeader, "Bar")
                header(expectedHeader, "Baz")
            }
        }
        then:
        noExceptionThrown()
    }

}
