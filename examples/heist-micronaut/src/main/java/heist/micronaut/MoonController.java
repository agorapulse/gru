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
package heist.micronaut;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.cookie.Cookie;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller("/moons")
class MoonController {

    private final MoonService moonService;

    public MoonController(MoonService moonService) {
        this.moonService = moonService;
    }

    @Get("/{planet}")
    public List<Moon> getMoons(@PathVariable("planet") String planet) {
        return Arrays.asList(moonService.get(planet, "moon"), moonService.get(planet, "mir"));
    }


    @Get("/{planet}/{moon}")
    public HttpResponse<Moon> sayHello(@PathVariable("planet") String planet, @PathVariable("moon") String moon) {
        if ("-".equals(planet)) {
            return HttpResponse.redirect(URI.create("/moons/earth/moon"));
        }
        return HttpResponse.ok(moonService.get(planet, moon));
    }

    @Get("/{planet}/{moon}/info")
    @Produces(MediaType.TEXT_PLAIN)
    public String info(@PathVariable("planet") String planet, @PathVariable("moon") String moon) {
        return moon + " goes around " + planet;
    }

    @Get("/{planet}/{moon}/html")
    @Produces(MediaType.TEXT_HTML)
    public String html(@PathVariable("planet") String planet, @PathVariable("moon") String moon) {
        return "<html><body>" + moon + " goes around " + planet + "</body></html>";
    }

    @Get("/the-big-cheese")
    public HttpResponse<?> sayHello() {
        return HttpResponse.redirect(URI.create("/moons/earth/moon"));
    }

    @Delete("/{planet}/{moon}")
    public HttpResponse<?> steal(String planet, String moon, @QueryValue String with) {
        if ("shrink-ray".equals(with)) {
            return HttpResponse.noContent();
        }
        return HttpResponse.badRequest();
    }

    @Post("/{planet}")
    public HttpResponse<?> create(String planet, String name) {
        return HttpResponse.ok(new Moon(name, planet));
    }

    @Get("/cookie")
    public HttpResponse<?> cookie(HttpRequest<?> request) {
        return HttpResponse.ok(request.getCookies().getAll().stream().collect(Collectors.toMap(Cookie::getName, Cookie::getValue)));
    }

    @Get("/setCookie")
    public HttpResponse<?> setCookie() {
        return HttpResponse.ok("OK")
                .cookie(Cookie.of("chocolate", "rules"))
                .cookie(Cookie.of("coffee", "lover").domain("localhost").secure(true));
    }

}
