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
package com.agorapulse.gru.grails.minions.jsonview;

import grails.plugin.json.view.test.JsonRenderResult;
import grails.views.api.http.Response;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Map;

class TestHttpResponse implements Response {

    private final JsonRenderResult result;

    TestHttpResponse(JsonRenderResult result) {
        this.result = result;
    }

    @Override
    public void header(String name, String value) {
        headers(Collections.singletonMap(name, value));
    }

    @Override
    public void header(Map<String, String> nameAndValue) {
        headers(nameAndValue);
    }

    @Override
    public void headers(Map<String, String> namesAndValues) {
        result.getHeaders().putAll(namesAndValues);
    }

    @Override
    public void contentType(String contentType) {
        result.setContentType(contentType);
    }

    @Override
    public void encoding(String encoding) {
        // ignore
    }

    @Override
    public void status(int status) {
        status(HttpStatus.valueOf(status));
    }

    @Override
    public void status(int status, String message) {
        status(HttpStatus.valueOf(status), message);
    }

    @Override
    public void status(HttpStatus status) {
        status(status, status.getReasonPhrase());
    }

    @Override
    public void status(HttpStatus status, String message) {
        result.setStatus(status);
        result.setMessage(message);
    }
}
