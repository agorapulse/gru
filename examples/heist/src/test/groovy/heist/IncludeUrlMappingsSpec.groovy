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
import com.agorapulse.gru.grails.Grails
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.AutoCleanup
import spock.lang.Specification

class IncludeUrlMappingsSpec extends Specification implements ControllerUnitTest<MoonController> {

    @AutoCleanup Gru<Grails<IncludeUrlMappingsSpec>> gru = Gru.create(Grails.create(this)).prepare {
        include ApiUrlMappings                                                          // <1>
    }

    void "moon is still there"() {
        expect:
            gru.test {
                delete '/api/v1/moons/earth/moon'                                       // <2>
                expect {
                    status BAD_REQUEST
                }
            }
    }
}
