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
package com.agorapulse.gru.grails

import com.agorapulse.gru.Client
import com.agorapulse.gru.cookie.Cookie
import org.grails.plugins.testing.GrailsMockHttpServletResponse

/**
 * Wrapper around mock Grails response.
 */
class GruGrailsResponse implements Client.Response {

    final GrailsMockHttpServletResponse response

    GruGrailsResponse(GrailsMockHttpServletResponse response) {
        this.response = response
    }

    @Override
    int getStatus() {
        return response.status
    }

    @Override
    List<String> getHeaders(String name) {
        return response.headers(name)
    }

    @Override
    String getText() {
        return response.text
    }

    @Override
    String getRedirectUrl() {
        return response.redirectUrl
    }

    @Override
    List<Cookie> getCookies() {
        List<javax.servlet.http.Cookie> cookies = response.cookies ? response.cookies.toList() : []

        return cookies.collect {
            Cookie.Builder builder = new Cookie.Builder()
                .name(it.name)
                .value(it.value)

            // TODO: expires?

            if (it.domain) {
                builder.domain(it.domain)
            }

            builder
                .httpOnly(it.httpOnly)
                .path(it.path)
                .secure(it.secure)
                .build()
        }
    }
}
