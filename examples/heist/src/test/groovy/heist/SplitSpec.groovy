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
package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.grails.Grails
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.AutoCleanup
import spock.lang.Specification

class SplitSpec extends Specification
    implements ControllerUnitTest<MoonController> {

    @AutoCleanup Gru gru = Gru.create(Grails.create(this)).prepare {
        include UrlMappings
    }

    // tag::whenThen[]
    void 'look at the moon'() {
        given:
            MoonService moonService = Mock(MoonService)
            controller.moonService = moonService                                            // <1>
        when:
            gru.test {                                                                      // <2>
                get '/moons/earth/moon'
            }
        then:
            gru.verify()                                                                    // <3>
            1 * moonService.findByPlanetAndName("earth", "moon") >> [name: "Moon"]          // <4>
    }
    // end::whenThen[]
}
