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
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.AutoCleanup
import spock.lang.Specification

class ManualMicronautSpec extends Specification implements ApplicationContextProvider {

    @AutoCleanup Gru gru = Gru.create(Micronaut.create(this))

    @AutoCleanup ApplicationContext applicationContext
    @AutoCleanup EmbeddedServer embeddedServer

    void setup() {
        applicationContext = ApplicationContext.builder().build()
        applicationContext.start()

        embeddedServer = applicationContext.getBean(EmbeddedServer)
        embeddedServer.start()
    }

    void 'test it works'() {
        expect:
            gru.test {
                get '/moons/earth/moon'
                expect {
                    json 'moon.json'
                    statuses OK, CREATED
                }
            }
    }

    void 'can verify redirects'() {
        expect:
            gru.test {
                get '/moons/the-big-cheese'
                expect {
                    redirect '/moons/earth/moon'
                }
            }
    }

}
