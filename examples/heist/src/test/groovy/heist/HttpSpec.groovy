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
package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.http.Http
import org.junit.Rule
import spock.lang.Specification

class HttpSpec extends Specification{

    @Rule Gru<Http> gru = Gru.equip(Http.steal(this))                                   // <1>
                             .prepare('https://despicableme.fandom.com')                // <2>

    void 'despicable me'() {
        expect:
            gru.test {
                get "/wiki/Felonius_Gru"                                                // <3>
            }
    }
}
