/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2025 Agorapulse.
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
package com.agorapulse.gru.okhttp

import com.agorapulse.gru.Gru
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.minions.AbstractMinion
import spock.lang.Specification

/**
 * Tests for Http.
 */
class HttpSpec extends Specification {

    void 'response is not set before the action is executed'() {
        when:
            Gru.create(OkHttp.create(this)).engage(new VioletMinion()).test {
                get '/foo/bar'
            }.verify()
        then:
            IllegalStateException error = thrown(IllegalStateException)
            error.message == 'Response hasn\'t been set yet'
    }

    static class VioletMinion extends AbstractMinion<OkHttp> {

        int index = 0

        VioletMinion() {
            super(OkHttp)
        }

        @Override
        protected GruContext doBeforeRun(OkHttp client, Squad squad, GruContext context) {
            client.response
            context
        }
    }

}
