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

import com.agorapulse.gru.AbstractClient
import com.agorapulse.gru.Client
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import spock.lang.Specification

/**
 * Tests for abstract minion.
 */
class AbstractMinionSpec extends Specification {

    void 'test type checking'() {
        given:
            AbstractMinion<MyClient> minion = new AbstractMinion<MyClient>(MyClient) {
                int index
            }
            MyOtherClient mock = new MyOtherClient(this)
            Squad squad = new Squad()
        when:
            GruContext beforeRun = minion.beforeRun(mock, squad, GruContext.EMPTY)
        then:
            beforeRun.hasError(ClassCastException)
        when:
            GruContext afterRun = minion.afterRun(mock, squad, GruContext.EMPTY)
        then:
            afterRun.hasError(ClassCastException)
        when:
            minion.verify(mock, squad, GruContext.EMPTY)
        then:
            thrown(AssertionError)
    }

    private static class MyClient extends AbstractClient {

        MyClient(Object unitTest) {
            super(unitTest)
        }

        Client.Request request
        Client.Response response

        @Override
        void reset() { }

        @Override
        GruContext run(Squad squad, GruContext context) { GruContext.EMPTY }
    }

    private static class MyOtherClient extends AbstractClient {

        MyOtherClient(Object unitTest) {
            super(unitTest)
        }

        Client.Request request
        Client.Response response

        @Override
        void reset() { }

        @Override
        GruContext run(Squad squad, GruContext context) { GruContext.EMPTY }
    }

}
