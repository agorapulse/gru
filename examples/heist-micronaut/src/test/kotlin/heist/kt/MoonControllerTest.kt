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
package heist.kt

import com.agorapulse.gru.kotlin.Gru
import com.agorapulse.gru.jsonunit.MatchesPattern
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import net.javacrumbs.jsonunit.core.Option
import org.junit.jupiter.api.Test

import  com.agorapulse.gru.HttpStatusShortcuts.NO_CONTENT

@MicronautTest
class MoonControllerTest {

    @Inject
    lateinit var gru: Gru

    // tag::stealWithShrinkRay[]
    @Test
    fun stealTheMoonWithShrinkRay() {
        gru.verify {
            delete("/moons/earth/moon") {
                param("with", "shrink-ray")
            }
            expect {
                status(NO_CONTENT)
            }
        }
    }
    // end::stealWithShrinkRay[]

    // tag::secretMoon[]
    @Test
    fun visitSecretMoonNoom() {
        gru.verify {
            get("/moons/earth/noom") {
                header("Authorization", "Felonius")
            }
        }
    }
    // end::secretMoon[]

    // tag::newMoon[]
    @Test
    fun createMoonForMargot() {
        gru.verify {
            post("/moons/earth") {
                json("newMoonRequest.json")
            }
        }
    }
    // end::newMoon[]

    // tag::newMoonGeneric[]
    @Test
    fun createMoonForMargotGeneric() {
        gru.verify {
            post("/moons/earth") {
                content("newMoonRequest.json", "application/json")
            }
        }
    }
    // end::newMoonGeneric[]

    // tag::fileUpload[]
    @Test
    fun uploadFile() {
        gru.verify {
            post("/upload") {
                upload {                                                                // <1>
                    param("message", "Hello")                                           // <2>
                    file(                                                               // <3>
                        "theFile",
                        "hello.txt",
                        inline("Hello World"),
                        "text/plain"
                    )
                }
            }
            expect {
                text(inline("11"))
            }
        }
    }
    // end::fileUpload[]

    // tag::cookies[]
    @Test
    fun sendCookies() {
        gru.verify {
            get("/moons/cookie") {
                cookie("chocolate", "rules")                                            // <1>
            }
            expect {
                json("cookies.json", Option.IGNORING_EXTRA_FIELDS)
            }
        }
    }

    @Test
    fun setCookies() {
        gru.verify {
            get("/moons/setCookie")
            expect {
                cookie("chocolate", "rules")                                            // <2>
                cookie {                                                                // <3>
                    name("coffee")
                    value("lover")
                    secure(true)
                    domain("localhost")
                }
            }
        }
    }
    // end::cookies[]

    // tag::jsonHeaders[]
    @Test
    fun jsonIsRendered() {
        gru.verify {
            get("/moons/earth/moon")
            expect { header("Content-Type", "application/json") }
        }
    }
    // end::jsonHeaders[]

    // tag::redirect[]
    @Test
    fun noPlanetNeeded() {
        gru.verify {
            get("/moons/-/moon")
            expect { redirect("/moons/earth/moon") }
        }
    }
    // end::redirect[]

    // tag::verifyText[]
    @Test
    fun infoIsRendered() {
        gru.verify {
            get("/moons/earth/moon/info")
            expect { text("textResponse.txt") }
        }
    }
    // end::verifyText[]

    // tag::verifyTextInline[]
    @Test
    fun infoIsRenderedInline() {
        gru.verify {
            get("/moons/earth/moon/info")
            expect { text(inline("moon goes around earth")) }
        }
    }
    // end::verifyTextInline[]

    // tag::verifyJson[]
    @Test
    fun verifyJson() {
        gru.verify {
            get("/moons/earth/moon")
            expect { json("moonResponse.json") }
        }
    }
    // end::verifyJson[]


    // tag::verifyJson2[]
    @Test
    fun verifyJsonWithOptions() {
        gru.verify {
            get("/moons/earth")
            expect { json("moonsResponse.json", Option.IGNORING_EXTRA_ARRAY_ITEMS) }
        }
    }
    // end::verifyJson2[]

    // tag::customiseJsonUnit[]
    @Test
    fun customiseJsonUnit() {
        gru.verify {
            get("/moons/earth/moon")
            expect {
                json("moonResponse.json")
                json {
                    withTolerance(0.1)
                    withMatcher(
                        "negativeIntegerString",
                        MatchesPattern.matchesPattern("-\\d+")
                    )
                }
            }
        }
    }
    // end::customiseJsonUnit[]

    // tag::verifyHtml[]
    @Test
    fun verifyHtml() {
        gru.verify {
            get("/moons/earth/moon/html")
            expect { html("htmlResponse.html") }
        }
    }
    // end::verifyHtml[]

}
