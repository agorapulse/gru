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
import io.micronaut.context.ApplicationContext
import io.micronaut.context.ApplicationContextProvider
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.Specification

import jakarta.inject.Inject

@MicronautTest
class NativeMicronautSpec extends Specification implements ApplicationContextProvider {

    Gru gru = Gru.create(Micronaut.create(this))

    @Inject ApplicationContext applicationContext

    void 'test it works'() {
        expect:
            gru.test {
                get '/moons/earth/moon'
                expect {
                    json 'moon.json'
                }
            }
    }

}
