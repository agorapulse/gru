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
import groovy.transform.CompileStatic
import spock.lang.Shared
import spock.lang.Specification

class GetPropertySpec extends Specification
    implements ControllerUnitTest<MoonController> {

    Gru gru = Gru.create(Grails.create(this)).prepare {
        include UrlMappings
    }

    @Shared POGO something = new POGO(value: 'earth')

    void 'look at the moon - using field'() {
        given:
            MoonService moonService = Mock(MoonService)
            controller.moonService = moonService
        when:
            gru.test {
                get "/moons/${something.value}/moon"
            }
        then:
            IllegalArgumentException ex = thrown(IllegalArgumentException)
            ex.message.contains('POGO aSomething = something')

            gru.verify()
    }

    void 'look at the moon - using local variable'() {
        given:
            MoonService moonService = Mock(MoonService)
            controller.moonService = moonService

            POGO sSomething = something
        when:
            gru.test {
                get "/moons/${sSomething.value}/moon"
            }
        then:
            gru.verify()

            1 * moonService.findByPlanetAndName('earth', 'moon') >> [name: 'Moon']
    }

}

@CompileStatic
class POGO {

    String value

}
