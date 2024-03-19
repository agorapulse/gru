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
package com.agorapulse.gru.http;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.cookie.Cookie;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

/**
 * Wrapper around OkHttp response.
 */
class GruHttpResponse implements Client.Response {

    private static final List<Integer> REDIRECT_STATUS_CODES = List.of(301, 302, 303, 307, 308);

    private final HttpResponse<String> response;

    public GruHttpResponse(HttpResponse<String> response) {
        this.response = response;
    }

    @Override
    public int getStatus() {
        Optional<HttpResponse<String>> priorResponse = response.previousResponse();

        if (priorResponse.isPresent() && REDIRECT_STATUS_CODES.contains(priorResponse.get().statusCode())) {
            return priorResponse.get().statusCode();
        }

        return response.statusCode();
    }

    @Override
    public List<String> getHeaders(String name) {
        Optional<HttpResponse<String>> priorResponse = response.previousResponse();

        if (priorResponse.isPresent() && REDIRECT_STATUS_CODES.contains(priorResponse.get().statusCode())) {
            return priorResponse.get().headers().allValues(name);
        }

        return response.headers().allValues(name);
    }

    @Override
    public String getText() {
        return response.body();
    }

    @Override
    public String getRedirectUrl() {
        return response.previousResponse()
            .flatMap(r -> r.headers().firstValue("location"))
            .or(() -> response.headers().firstValue("location"))
            .orElse(null);
    }

    @Override
    public List<Cookie> getCookies() {
        Optional<HttpResponse<String>> priorResponse = response.previousResponse();

        if (priorResponse.isPresent() && REDIRECT_STATUS_CODES.contains(priorResponse.get().statusCode())) {
            return Cookie.parseAll(priorResponse.get().headers().allValues("Set-Cookie"));
        }

        return Client.Response.super.getCookies();
    }
}
