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
import com.agorapulse.gru.MultipartDefinition;
import com.agorapulse.gru.TestDefinitionBuilder;
import com.agorapulse.gru.cookie.Cookie;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.multipart.MultipartBody;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Wrapper around OkHttp request.
 */
class MicronautHttpRequest implements Client.Request {

    private final Map<String, String> parameters = new LinkedHashMap<>();
    private final Map<String, List<String>> headers = new LinkedHashMap<>();

    private final List<Cookie> cookies = new ArrayList<>();
    private Object body;

    private String baseUri;
    private String method = TestDefinitionBuilder.GET;
    private String uri;
    private String contentType = MediaType.APPLICATION_JSON;

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
    public String getMethod() {
        return method;
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
        headers.computeIfAbsent(name, n -> new ArrayList<>()).add(value);
    }

    @Override
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    @Override
    public void setJson(String jsonText) {
        contentType = MediaType.APPLICATION_JSON;
        body = jsonText;
    }

    @Override
    public void setContent(String contentType, byte[] payload) {
        this.contentType = contentType;
        this.body = payload;
    }

    @Override
    public void setMultipart(MultipartDefinition definition) {
        contentType = MediaType.MULTIPART_FORM_DATA;

        MultipartBody.Builder builder = MultipartBody.builder();

        definition.getParameters().forEach((k, v) -> builder.addPart(k, v != null ? String.valueOf(v) : null));

        definition.getFiles().forEach((k, f) ->
            builder.addPart(
                f.getParameterName(),
                f.getFilename(),
                MediaType.of(f.getContentType()),
                f.getBytes()
            )
        );

        body = builder.build();
    }

    @Override
    public void addParameter(String name, Object value) {
        parameters.put(name, value == null ? "" : value.toString());
    }

    HttpRequest<?> buildHttpRequest() {
        MutableHttpRequest<?> builder;
        if (!parameters.isEmpty()) {
            if (TestDefinitionBuilder.HAS_URI_PARAMETERS.contains(method) || body != null) {
                builder = HttpRequest.create(HttpMethod.parse(method), buildUri(baseUri, uri, parameters).toString());
                if (body != null) {
                    builder.body(body);
                }
            } else {
                StringBuilder params = new StringBuilder();
                appendParameters(parameters, params);
                builder = HttpRequest.create(HttpMethod.parse(method), buildUri(baseUri, uri, Collections.emptyMap()).toString());
                contentType = MediaType.APPLICATION_FORM_URLENCODED;
                builder.body(body);
            }
        } else if (TestDefinitionBuilder.REQUIRES_BODY.contains(method)) {
            builder = HttpRequest.create(HttpMethod.parse(method), buildUri(baseUri, uri, Collections.emptyMap()).toString());
            builder.body(body);
        } else {
            builder = HttpRequest.create(HttpMethod.parse(method), buildUri(baseUri, uri, Collections.emptyMap()).toString());
            builder.body(body);
        }

        builder.contentType(contentType);

        headers.forEach((name, values) -> {
            values.forEach(value -> builder.getHeaders().add(name, value));
        });

        cookies.forEach(c -> {
            io.micronaut.http.cookie.Cookie cookie = io.micronaut.http.cookie.Cookie.of(c.getName(), c.getValue());
            cookie.httpOnly(c.getHttpOnly());
            cookie.secure(c.getSecure());
            cookie.maxAge(c.getExpiresAt());
            cookie.domain(c.getDomain());
            cookie.path(c.getPath());

            builder.cookie(cookie);
        });

        return builder;
    }

    private static URI buildUri(String baseUri, String uri, Map<String, String> parameters) {
        StringBuilder uriBuilder = new StringBuilder();
        if (baseUri != null) {
            if (baseUri.endsWith("/")) {
                uriBuilder.append(baseUri, 0, baseUri.length() - 1);
            } else {
                uriBuilder.append(baseUri);
            }
        }

        if (uri != null) {
            if (baseUri == null && !uri.startsWith("http")) {
                uriBuilder.append("http://localhost:8080");
            }
            if (uri.startsWith("/")) {
                uriBuilder.append(uri);
            } else if (uri.startsWith("http")) {
                uriBuilder.append(uri);
            } else {
                uriBuilder.append("/").append(uri);
            }
        }

        if (!parameters.isEmpty()) {
            uriBuilder.append("?");
            appendParameters(parameters, uriBuilder);
        }

        return URI.create(uriBuilder.toString());
    }

    private static void appendParameters(Map<String, String> parameters, StringBuilder uriBuilder) {
        parameters.forEach((k, v) -> uriBuilder.append(URLEncoder.encode(k, StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(v, StandardCharsets.UTF_8)).append("&"));
        uriBuilder.deleteCharAt(uriBuilder.length() - 1);
    }

}
