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
import com.agorapulse.gru.micronaut.Micronaut
import heist.micronaut.Moon
import heist.micronaut.MoonService
import io.micronaut.context.env.Environment
import spock.lang.AutoCleanup
import spock.lang.Specification

import javax.inject.Inject

class AdvancedAutomaticMicronautSpec extends Specification {

    MoonService moonService = Mock()                                                    // <1>

    @AutoCleanup Gru gru = Gru.create(
        Micronaut.build(this) {
            environments 'my-custom-env'                                                // <2>
        }.then {
            registerSingleton(MoonService, moonService)                                 // <3>
        }.start()                                                                       // <4>
    )

    @Inject Environment environment                                                     // <5>

    void 'test it works'() {
        expect:
            'my-custom-env' in environment.activeNames
        when:
            gru.test {
                get '/moons/earth/moon'
                expect {
                    json 'moon.json'
                }
            }
        then:
            gru.verify()

            1 * moonService.get('earth', 'moon') >> new Moon('earth', 'moon')
    }

}
