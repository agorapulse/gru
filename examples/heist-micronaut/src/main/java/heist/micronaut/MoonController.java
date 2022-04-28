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
package heist.micronaut;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.cookie.Cookie;

import java.net.URI;
import java.util.stream.Collectors;

@Controller("/moons")
class MoonController {

    private final MoonService moonService;

    public MoonController(MoonService moonService) {
        this.moonService = moonService;
    }

    @Get("/{planet}/{moon}")
    Moon sayHello(@PathVariable("planet") String planet, @PathVariable("moon") String moon) {
        return moonService.get(planet, moon);
    }

    @Get("/the-big-cheese")
    HttpResponse<?> sayHello() {
        return HttpResponse.redirect(URI.create("/moons/earth/moon"));
    }

    @Delete("/{planet}/{moon}")
    HttpResponse<?> steal(String planet, String moon, @QueryValue String with) {
        if ("shrink-ray".equals(with)) {
            return HttpResponse.noContent();
        }
        return HttpResponse.badRequest();
    }

    @Post("/{planet}")
    HttpResponse<?> create(String planet, String name) {
        return HttpResponse.ok(new Moon(name, planet));
    }

    @Get("/cookie")
    HttpResponse<?> cookie(HttpRequest<?> request) {
        return HttpResponse.ok(request.getCookies().getAll().stream().collect(Collectors.toMap(Cookie::getName, Cookie::getValue)));
    }

    @Get("/setCookie")
    HttpResponse<?> setCookie() {
        return HttpResponse.ok("OK")
                .cookie(Cookie.of("chocolate", "rules"))
                .cookie(Cookie.of("coffee", "lover").domain("localhost").secure(true));
    }

}
