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
package com.agorapulse.gru.grails.minions.jsonview

import grails.plugin.json.view.test.JsonRenderResult
import grails.views.api.http.Response
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.springframework.http.HttpStatus

/**
 * Test HTTP response.
 */
@CompileStatic @PackageScope
class TestHttpResponse implements Response {
    final JsonRenderResult result

    TestHttpResponse(JsonRenderResult result) {
        this.result = result
    }

    @Override
    void header(String name, String value) {
        header((name): value)
    }

    @Override
    void header(Map<String, String> nameAndValue) {
        headers(nameAndValue)
    }

    @Override
    void headers(Map<String, String> namesAndValues) {
        result.headers.putAll(namesAndValues)
    }

    @Override
    void contentType(String contentType) {
        result.contentType = contentType
    }

    @Override
    void encoding(String encoding) {
        // ignore
    }

    @Override
    void status(int status) {
        this.status(HttpStatus.valueOf(status))
    }

    @Override
    void status(int status, String message) {
        this.status(HttpStatus.valueOf(status), message)
    }

    @Override
    void status(HttpStatus status) {
        this.status(status, status.reasonPhrase)
    }

    @Override
    void status(HttpStatus status, String message) {
        result.status = status
        result.message = message
    }
}
