/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2026 Agorapulse.
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
package com.agorapulse.gru.grails;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.cookie.Cookie;
import org.grails.plugins.testing.GrailsMockHttpServletResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper around mock Grails response.
 */
public class GruGrailsResponse implements Client.Response {

    private final GrailsMockHttpServletResponse response;

    public GruGrailsResponse(GrailsMockHttpServletResponse response) {
        this.response = response;
    }

    public GrailsMockHttpServletResponse getResponse() {
        return response;
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
        return response.getText();
    }

    @Override
    public String getRedirectUrl() {
        return response.getRedirectUrl();
    }

    @Override
    public List<Cookie> getCookies() {
        jakarta.servlet.http.Cookie[] raw = response.getCookies();
        List<Cookie> result = new ArrayList<>();
        if (raw == null) {
            return result;
        }
        for (jakarta.servlet.http.Cookie c : raw) {
            Cookie.Builder builder = new Cookie.Builder()
                .name(c.getName())
                .value(c.getValue());
            if (c.getDomain() != null) {
                builder.domain(c.getDomain());
            }
            result.add(builder
                .httpOnly(c.isHttpOnly())
                .path(c.getPath())
                .secure(c.getSecure())
                .build());
        }
        return result;
    }
}
