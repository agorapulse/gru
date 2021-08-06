/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2021 Agorapulse.
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
package com.agorapulse.gru.spring

import com.agorapulse.gru.Client
import com.agorapulse.gru.cookie.Cookie
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.springframework.mock.web.MockHttpServletResponse

/**
 * Gru wrapper around Spring mock response.
 */
@PackageScope
@CompileStatic
class GruSpringResponse implements Client.Response {

    private final MockHttpServletResponse response

    GruSpringResponse(MockHttpServletResponse response) {
        this.response = response
    }

    @Override
    int getStatus() {
        return response.status
    }

    @Override
    List<String> getHeaders(String name) {
        return response.getHeaders(name)
    }

    @Override
    String getText() {
        return response.contentAsString
    }

    @Override
    String getRedirectUrl() {
        return response.redirectedUrl
    }

    @Override
    @SuppressWarnings('ExplicitArrayListInstantiation')
    List<Cookie> getCookies() {
        List<javax.servlet.http.Cookie> cookies = response.cookies ? response.cookies.toList() : new ArrayList<javax.servlet.http.Cookie>()

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
