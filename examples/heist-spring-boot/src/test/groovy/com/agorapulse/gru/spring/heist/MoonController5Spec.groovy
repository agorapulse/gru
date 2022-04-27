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
package com.agorapulse.gru.spring.heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.spring.Spring
import com.agorapulse.gru.spring.minions.ResultMatcherMinion
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.AutoCleanup
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest
class MoonController5Spec extends Specification {

    @Autowired MockMvc mvc

    @AutoCleanup Gru<Spring> gru = Gru.create(Spring.create(this))
                                      .engage(new ResultMatcherMinion())
                                      .engage(new NastyMinion())

    void 'nasty minion'() {
        when:
            gru.test {
                get '/moons/earth/moon'
                expect {
                    that status().isOk()
                }
            }.verify()
        then:
            AssertionError error = thrown(AssertionError)
            error.message
            error.message.startsWith('Cannot perform assertion')
    }

}
