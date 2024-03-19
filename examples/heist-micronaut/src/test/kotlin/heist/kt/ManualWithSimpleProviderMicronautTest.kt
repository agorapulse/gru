/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2024 Agorapulse.
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
package heist.kt

import com.agorapulse.gru.kotlin.create
import com.agorapulse.gru.micronaut.Micronaut
import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ManualWithSimpleProviderMicronautTest {

    private val gru = create(Micronaut.create(this) { context })                        // <1>

    private lateinit var context: ApplicationContext
    private lateinit var embeddedServer: EmbeddedServer

    @BeforeEach
    fun setup() {
        context = ApplicationContext.builder().build()
        context.start()

        embeddedServer = context.getBean(EmbeddedServer::class.java)
        embeddedServer.start()
    }

    @AfterEach
    fun cleanUp() {
        embeddedServer.close()
        context.close()
    }


    @Test
    fun testMoon() {
        gru.verify {
            get("/moons/earth/moon")
            expect {
                json("moon.json")
            }
        }
    }

}
