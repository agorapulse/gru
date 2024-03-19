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
package com.agorapulse.gru.spring.heist.kt;

import com.agorapulse.gru.kotlin.create;
import com.agorapulse.gru.spring.Spring;
import com.agorapulse.gru.spring.minions.RequestBuilderMinion;
import com.agorapulse.gru.spring.minions.ResultMatcherMinion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest
class MoonControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    val gru = create(Spring.create(this));

    // tag::mockmvc[]
    @Test
    fun jsonIsRendered() {
        gru.verify {
            get("/moons/earth/moon") {
                command<RequestBuilderMinion> {                                         // <1>
                    addBuildStep { mock ->
                        mock.accept(MediaType.APPLICATION_JSON_UTF8)                    // <2>
                            .locale(Locale.CANADA)
                    }
                }
            }
            expect {
                header("Content-Type", "application/json;charset=UTF-8")
                json("moonResponse.json")
                command<ResultMatcherMinion> {                                          // <3>
                    addMatcher(content().encoding("UTF-8"))                             // <4>
                    addMatcher(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                }
            }
        }
    }
    // end::mockmvc[]

}
