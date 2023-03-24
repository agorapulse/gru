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
package com.agorapulse.gru.spring;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.MultipartDefinition;
import com.agorapulse.gru.TestDefinitionBuilder;
import com.agorapulse.gru.cookie.Cookie;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Gru wrapper around Spring mock request.
 */
public class GruSpringRequest implements Client.Request {

    final List<Consumer<MockHttpServletRequestBuilder>> steps = new ArrayList<>();

    private String baseUri;
    private String method = TestDefinitionBuilder.GET;
    private String uri;
    private MultipartDefinition multipart;

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getBaseUri() {
        return baseUri;
    }

    @Override
    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public void addHeader(String name, String value) {
        addBuildStep(b -> b.header(name, value));
    }

    @Override
    public void setJson(String jsonText) {
        addBuildStep(b -> b.contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonText));
    }

    @Override
    public void setContent(String mediaType, byte[] payload) {
        addBuildStep(b -> b.contentType(MediaType.parseMediaType(mediaType)).content(payload));
    }

    @Override
    public void addParameter(String name, Object value) {
        addBuildStep(b -> b.param(name, value == null ? null : value.toString()));
    }

    public void addBuildStep(Consumer<MockHttpServletRequestBuilder> step) {
        steps.add(step);
    }

    @Override
    public void setMultipart(MultipartDefinition definition) {
        this.multipart = definition;
    }

    public List<Consumer<MockHttpServletRequestBuilder>> getSteps() {
        return steps;
    }

    public MultipartDefinition getMultipart() {
        return multipart;
    }

    @Override
    public void addCookie(Cookie c) {
        addBuildStep(b -> b.cookie(new javax.servlet.http.Cookie(c.getName(), c.getValue())));
    }
}
