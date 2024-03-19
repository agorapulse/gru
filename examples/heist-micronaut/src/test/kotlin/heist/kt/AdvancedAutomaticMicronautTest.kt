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
import heist.micronaut.Moon
import heist.micronaut.MoonService
import io.micronaut.context.env.Environment
import jakarta.inject.Inject
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.Mockito.*

class AdvancedAutomaticMicronautTest {

    private val moonService = mock(MoonService::class.java)                             // <1>

    private val gru = create(
        Micronaut.build(this)
            .doWithContextBuilder { b ->
                b.environments("my-custom-env")                                         // <2>
            }.doWithContext { c ->
                c.registerSingleton(MoonService::class.java, moonService)               // <3>
            }.start()                                                                   // <4>
    )

    @Inject
    private lateinit var environment: Environment                                       // <5>

    @Test
    fun testMoon() {
        assertNotNull(environment)

        assertTrue(environment.activeNames.contains("test"))
        assertTrue(environment.activeNames.contains("my-custom-env"))

        `when`(moonService.get("earth", "moon")).thenReturn(Moon("earth", "moon"))

        gru.verify {
            get("/moons/earth/moon")
            expect {
                json("moon.json")
            }
        }

        verify(moonService, times(1)).get("earth", "moon")
    }

}
