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
package com.agorapulse.gru

import org.junit.runner.Description
import org.junit.runners.model.Statement
import spock.lang.Specification

/**
 * Tests for Gru.
 */
class GruSpec extends Specification {

    @SuppressWarnings('UnnecessaryGetter')
    void 'expectations are verified'() {
        given:
            Description description = Description.createTestDescription(GruSpec, 'test is verified')
            Statement statement = new Statement() { @Override void evaluate() throws Throwable { } }
            Client.Request request = Mock(Client.Request)
            Client client = Mock(Client) {
                getInitialSquad() >> []
                getRequest() >> request
                run(_, _) >> GruContext.EMPTY
            }
            Gru gru = Gru.equip(client)
        when:
            gru.test {
                get '/foo/bar'
            }
            gru.apply(statement, description).evaluate()
        then:
            AssertionError e = thrown(AssertionError)
            e.message?.startsWith('Test wasn\'t verified.')
    }

}
