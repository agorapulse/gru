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
package heist.json.views

import com.agorapulse.gru.Gru
import com.agorapulse.gru.grails.Grails
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.AutoCleanup
import spock.lang.Specification

class JsonControllerSpec extends Specification implements ControllerUnitTest<JsonController> {

    @AutoCleanup Gru gru = Gru.create(Grails.create(this)).prepare {
        include UrlMappings
    }

    void 'render moons'() {
        expect:
            gru.test {
                get '/json'
                expect {
                    status NON_AUTHORITATIVE_INFORMATION
                    headers foo: 'bar', 'Content-Type': 'x-application/moons'
                    json([[name: "Moon", planet: "Earth"]])
                }
            }
    }

    void 'render moon'() {
        expect:
            gru.test {
                get '/json/moon'
                expect {
                    json name: "Moon", planet: "Earth"
                }
            }
    }

    void 'render moon (using converter)'() {
        expect:
            gru.test {
                get '/json/moon', {
                    executes controller.&show
                    param 'manual', 'true'
                }
                expect {
                    json 'moon.json'
                }
            }
    }

    void 'render with missing template'() {
        when:
            gru.test {
                get '/json/missing'
                expect {
                    json 'moon.json'
                }
            }.verify()
        then:
            AssertionError error = thrown(AssertionError)
            error.toString().contains('No view or template found for URI /json/missing')
    }

}
