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
package heist.micronaut;

import com.agorapulse.gru.Gru;
import com.agorapulse.gru.jsonunit.MatchesPattern;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import net.javacrumbs.jsonunit.core.Option;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import static com.agorapulse.gru.Content.inline;
import static com.agorapulse.gru.HttpStatusShortcuts.NO_CONTENT;

@MicronautTest
public class MoonControllerTest {

    @Inject
    Gru gru;

    // tag::stealWithShrinkRay[]
    @Test
    public void stealTheMoonWithShrinkRay() throws Throwable {
        gru.verify(test -> test
            .delete("/moons/earth/moon", req -> req.param("with", "shrink-ray"))
            .expect(resp -> resp.status(NO_CONTENT))
        );
    }
    // end::stealWithShrinkRay[]

    // tag::secretMoon[]
    @Test
    public void visitSecretMoonNoom() throws Throwable {
        gru.verify(test -> test
            .get("/moons/earth/noom", req -> req.header("Authorization", "Felonius"))
        );
    }
    // end::secretMoon[]

    // tag::newMoon[]
    @Test
    public void createMoonForMargot() throws Throwable {
        gru.verify(test -> test.
            post("/moons/earth", req -> req.json("newMoonRequest.json"))
        );
    }
    // end::newMoon[]

    // tag::newMoonGeneric[]
    @Test
    public void createMoonForMargotGeneric() throws Throwable {
        gru.verify(test -> test.
            post("/moons/earth", req -> req.content("newMoonRequest.json", "application/json"))
        );
    }
    // end::newMoonGeneric[]

    // tag::fileUpload[]
    @Test
    public void uploadFile() throws Throwable {
        gru.verify(test -> test
            .post(
                "/upload",
                req -> req.upload(u -> u
                    .param("message", "Hello")
                    .file(
                        "theFile",
                        "hello.txt",
                        inline("Hello World"),
                        "text/plain"
                    )
                )
            )
            .expect(resp -> resp.text(inline("11")))
        );
    }
    // end::fileUpload[]

    // tag::cookies[]
    @Test
    public void sendCookies() throws Throwable {
        gru.verify(test -> test
            .get("/moons/cookie", req -> req.
                cookie("chocolate", "rules")                                            // <1>
            )
            .expect(resp -> resp.
                json("cookies.json", Option.IGNORING_EXTRA_FIELDS)
            )
        );
    }

    @Test
    public void setCookies() throws Throwable {
        gru.verify(test -> test
            .get("/moons/setCookie")
            .expect(resp -> resp
                .cookie("chocolate", "rules")                                           // <2>
                .cookie(c -> c                                                          // <3>
                    .name("coffee")
                    .value("lover")
                    .secure(true)
                    .domain("localhost")
                )
            )
        );
    }
    // end::cookies[]

    // tag::jsonHeaders[]
    @Test
    public void jsonIsRendered() throws Throwable {
        gru.verify(test -> test
            .get("/moons/earth/moon")
            .expect(resp -> resp.header("Content-Type", "application/json"))
        );
    }
    // end::jsonHeaders[]

    // tag::redirect[]
    @Test
    public void noPlanetNeeded() throws Throwable {
        gru.verify(test -> test
            .get("/moons/-/moon")
            .expect(resp -> resp.redirect("/moons/earth/moon"))
        );
    }
    // end::redirect[]

    // tag::verifyText[]
    @Test
    public void infoIsRendered() throws Throwable {
        gru.verify(test -> test
            .get("/moons/earth/moon/info")
            .expect(resp -> resp.text("textResponse.txt"))
        );
    }
    // end::verifyText[]

    // tag::verifyTextInline[]
    @Test
    public void infoIsRenderedInline() throws Throwable {
        gru.verify(test -> test
            .get("/moons/earth/moon/info")
            .expect(resp -> resp.text(inline("moon goes around earth")))
        );
    }
    // end::verifyTextInline[]

    // tag::verifyJson[]
    @Test
    public void verifyJson() throws Throwable {
        gru.verify(test -> test
            .get("/moons/earth/moon")
            .expect(resp -> resp.json("moonResponse.json"))
        );
    }
    // end::verifyJson[]


    // tag::verifyJson2[]
    @Test
    public void verifyJsonWithOptions() throws Throwable {
        gru.verify(test -> test
            .get("/moons/earth")
            .expect(resp -> resp.json("moonsResponse.json", Option.IGNORING_EXTRA_ARRAY_ITEMS))
        );
    }
    // end::verifyJson2[]

    // tag::customiseJsonUnit[]
    @Test
    public void customiseJsonUnit() throws Throwable {
        gru.verify(test -> test
            .get("/moons/earth/moon")
            .expect(resp -> resp
                .json("moonResponse.json")
                .json(json -> json
                    .withTolerance(0.1)
                    .withMatcher(
                        "negativeIntegerString",
                        MatchesPattern.matchesPattern("-\\d+")
                    )
                )
            )
        );
    }
    // end::customiseJsonUnit[]

    // tag::verifyHtml[]
    @Test
    public void verifyHtml() throws Throwable {
        gru.verify(test -> test
            .get("/moons/earth/moon/html")
            .expect(resp -> resp.html("htmlResponse.html"))
        );
    }
    // end::verifyHtml[]

}
