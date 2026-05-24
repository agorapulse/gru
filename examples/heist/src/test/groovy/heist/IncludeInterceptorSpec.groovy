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
package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.grails.Grails
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class IncludeInterceptorSpec extends Specification implements ControllerUnitTest<MoonController> {

    Gru gru = Gru.create(Grails.create(this)).prepare {
        include ApiUrlMappings
        include VectorInterceptor, true                                                 // <1>
    }

    void "moon is still there"() {
        given:
            defineBeans {
                vectorMessage(VectorMessage, "Le Vecteur était là!")                    // <2>
            }
        expect:
            gru.test {
                delete '/api/v1/moons/earth/moon'
                expect {
                    status NOT_FOUND
                    headers 'X-Message': "Le Vecteur était là!"                         // <3>
                }
            }
    }

}
