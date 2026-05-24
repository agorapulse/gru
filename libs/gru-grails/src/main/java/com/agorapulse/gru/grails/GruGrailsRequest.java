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
import com.agorapulse.gru.MultipartDefinition;
import com.agorapulse.gru.cookie.Cookie;
import grails.testing.web.controllers.ControllerUnitTest;
import org.grails.plugins.testing.GrailsMockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Wrapper around Grails mock request.
 */
public class GruGrailsRequest implements Client.Request {

    private final GrailsMockHttpServletRequest request;
    private final ControllerUnitTest<?> unitTest;

    private String baseUri = "";
    private String uri = "";

    public GruGrailsRequest(GrailsMockHttpServletRequest request, ControllerUnitTest<?> unitTest) {
        this.request = request;
        this.unitTest = unitTest;
    }

    @Override
    public String getBaseUri() {
        return baseUri;
    }

    @Override
    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
        updateRequestUri();
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public void setUri(String uri) {
        this.uri = uri;
        updateRequestUri();
    }

    @Override
    public void setMethod(String method) {
        request.setMethod(method);
    }

    @Override
    public String getMethod() {
        return request.getMethod();
    }

    @Override
    public void addHeader(String name, String value) {
        request.addHeader(name, value);
    }

    @Override
    public void addCookie(Cookie cookie) {
        jakarta.servlet.http.Cookie[] existing = request.getCookies();
        List<jakarta.servlet.http.Cookie> cookies = existing != null
            ? new ArrayList<>(java.util.Arrays.asList(existing))
            : new ArrayList<>();

        // request cookies only carry name and value
        cookies.add(new jakarta.servlet.http.Cookie(cookie.getName(), cookie.getValue()));

        request.setCookies(cookies.toArray(new jakarta.servlet.http.Cookie[0]));
    }

    @Override
    public void setJson(String jsonText) {
        request.setJson(jsonText);
    }

    @Override
    public void setContent(String contentType, byte[] content) {
        request.setContent(content);
        request.setContentType(contentType);
    }

    @Override
    public void addParameter(String name, Object value) {
        unitTest.getParams().put(name, value);
    }

    @Override
    public void setMultipart(MultipartDefinition definition) {
        if (definition.getParameters() != null) {
            definition.getParameters().forEach((k, v) ->
                unitTest.getParams().put(k, v == null ? null : String.valueOf(v)));
        }
        if (definition.getFiles() != null) {
            definition.getFiles().forEach((k, f) ->
                request.addFile(new MockMultipartFile(
                    f.getParameterName(),
                    f.getFilename(),
                    f.getContentType(),
                    f.getBytes()
                )));
        }
    }

    private void updateRequestUri() {
        unitTest.getRequest().setRequestURI((baseUri + uri).replaceAll("/+", "/"));
    }
}
