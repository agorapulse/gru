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
package com.agorapulse.gru.spring;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.cookie.Cookie;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gru wrapper around Spring mock response.
 */
public class GruSpringResponse implements Client.Response {

    private final MockHttpServletResponse response;

    public GruSpringResponse(MockHttpServletResponse response) {
        this.response = response;
    }

    @Override
    public int getStatus() {
        return response.getStatus();
    }

    @Override
    public List<String> getHeaders(String name) {
        return response.getHeaders(name);
    }

    @Override
    public String getText() {
        try {
            return response.getContentAsString();
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Cannot read content from the response", e);
        }
    }

    @Override
    public String getRedirectUrl() {
        return response.getRedirectedUrl();
    }

    @Override
    public List<Cookie> getCookies() {
        List<javax.servlet.http.Cookie> cookies = response.getCookies() != null ? Arrays.asList(response.getCookies()) : Collections.emptyList();

        return cookies.stream().map(it -> {
            Cookie.Builder builder = new Cookie.Builder()
                .name(it.getName())
                .value(it.getValue());

            // TODO: expires?

            if (it.getDomain() != null) {
                builder.domain(it.getDomain());
            }

            return builder
                .httpOnly(it.isHttpOnly())
                .path(it.getPath())
                .secure(it.getSecure())
                .build();
        }).collect(Collectors.toList());
    }

}
