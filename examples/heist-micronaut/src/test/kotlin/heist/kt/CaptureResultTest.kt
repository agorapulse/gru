/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2025 Agorapulse.
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

import com.agorapulse.gru.kotlin.Gru
import com.agorapulse.gru.minions.JsonMinion
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@MicronautTest
class CaptureResultTest {

    @Inject lateinit var gru: Gru

    // tag::extractResponseText[]
    @Test
    fun testGet() {
        gru.test {                                                                      // <1>
            get("/moons/earth/moon")
            expect { json("moon.json") }
        }

        gru.verify()                                                                    // <2>

        val responseText = gru.squad.ask<JsonMinion, String> {
            responseText                                                                // <3>
        }

        Assertions.assertNotNull(responseText)
        Assertions.assertTrue(responseText!!.contains("moon"))                          // <4>

        gru.close()                                                                     // <5>
    }
    // end::extractResponseText[]

}
