/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2023 Agorapulse.
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
package com.agorapulse.gru

import spock.lang.Specification

/**
 * Tests for Gru.
 */
class GruSpec extends Specification {

    @SuppressWarnings('UnnecessaryGetter')
    void 'expectations are verified'() {
        given:
            Client.Request request = Mock(Client.Request)
            Client client = Mock(Client) {
                getInitialSquad() >> []
                getRequest() >> request
                run(_, _) >> GruContext.EMPTY
            }
            Gru gru = Gru.create(client)
        when:
            gru.test {
                get '/foo/bar'
            }
            gru.close()
        then:
            AssertionError e = thrown(AssertionError)
            e.message?.startsWith('Test wasn\'t verified.')
    }

}
