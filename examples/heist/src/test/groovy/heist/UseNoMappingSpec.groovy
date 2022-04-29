/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2022 Agorapulse.
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

class UseNoMappingSpec extends Specification implements ControllerUnitTest<MoonController> {

    @AutoCleanup Gru gru = Gru.create(Grails.create(this))

    void 'look at the moon'() {
        when:
            gru.test {
                baseUri '/moons'
                get '/earth/moon'
            }.verify()
        then:
            AssertionError ex = thrown(AssertionError)
            ex.message == 'URI for action is specified but UrlMappings is not defined nor default UrlMappings class exists!'
    }

    void setup() {
        controller.moonService = new MoonService()
    }
}
