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
package com.agorapulse.gru.micronaut.http;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.cookie.Cookie;
import io.micronaut.http.HttpResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Wrapper around OkHttp response.
 */
class MicronautHttpResponse implements Client.Response {

    private final HttpResponse<?> response;

    public MicronautHttpResponse(HttpResponse<?> response) {
        this.response = response;
    }

    @Override
    public int getStatus() {
        return response.code();
    }

    @Override
    public List<String> getHeaders(String name) {
        return response.getHeaders().getAll(name);
    }

    @Override
    public String getText() {
        return response.getBody(String.class).orElse(null);
    }

    @Override
    public String getRedirectUrl() {
        return response.getHeaders().get("location");
    }

    @Override
    public List<Cookie> getCookies() {
        return response.getCookies().values().stream().map(c -> {
            Cookie.Builder builder = new Cookie.Builder()
                .name(c.getName())
                .value(c.getValue());

            if (c.getDomain() != null) {
                builder.domain(c.getDomain());
            }

            if (c.getPath() != null) {
                builder.path(c.getPath());
            }

            if (c.getMaxAge() > 0) {
                builder.expiresAt(c.getMaxAge());
            }

            if (c.isHttpOnly()) {
                builder.httpOnly(c.isHttpOnly());
            }

            if (c.isSecure()) {
                builder.secure(c.isSecure());
            }

            return builder.build();
        }).collect(Collectors.toList());
    }
}
