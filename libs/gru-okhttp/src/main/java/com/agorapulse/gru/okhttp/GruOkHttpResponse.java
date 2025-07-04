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
package com.agorapulse.gru.okhttp;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.cookie.Cookie;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.List;

/**
 * Wrapper around OkHttp response.
 */
class GruOkHttpResponse implements Client.Response {

    private final Response response;
    private final boolean followRedirects;

    public GruOkHttpResponse(Response response, boolean followRedirects) {
        this.response = response;
        this.followRedirects = followRedirects;
    }

    @Override
    public int getStatus() {
        Response priorResponse = response.priorResponse();

        if (followRedirects && priorResponse != null && priorResponse.isRedirect()) {
            return priorResponse.code();
        }

        return response.code();
    }

    @Override
    public List<String> getHeaders(String name) {
        if (followRedirects && response.priorResponse() != null && response.priorResponse().isRedirect()) {
            return response.priorResponse().headers(name);
        }
        return response.headers(name);
    }

    @Override
    public String getText() {
        try {
            ResponseBody body = response.body();
            return body == null ? null : body.string();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read response body", e);
        }
    }

    @Override
    public String getRedirectUrl() {
        if (followRedirects) {
            Response priorResponse = response.priorResponse();
            return priorResponse == null ? null : priorResponse.header("Location");
        }
        return response.header("Location");
    }

    @Override
    public List<Cookie> getCookies() {
        if (followRedirects && response.priorResponse() != null && response.priorResponse().isRedirect()) {
            return Cookie.parseAll(response.priorResponse().headers("Set-Cookie"));
        }
        return Client.Response.super.getCookies();
    }
}
