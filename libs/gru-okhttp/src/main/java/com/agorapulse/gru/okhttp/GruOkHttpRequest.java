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
import com.agorapulse.gru.MultipartDefinition;
import com.agorapulse.gru.TestDefinitionBuilder;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.internal.http.HttpMethod;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Wrapper around OkHttp request.
 */
class GruOkHttpRequest implements Client.Request {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final Map<String, String> parameters = new LinkedHashMap<>();
    private final Request.Builder builder = new Request.Builder();
    private RequestBody body;

    private String baseUri;
    private String method = TestDefinitionBuilder.GET;
    private String uri;

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
        return null;
    }

    @Override
    public String getMethod() {
        return null;
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
        builder.addHeader(name, value);
    }

    @Override
    public void setJson(String jsonText) {
        body = RequestBody.create(JSON, jsonText);
    }

    @Override
    public void setContent(String contentType, byte[] payload) {
        body = RequestBody.create(MediaType.parse(contentType), payload);
    }

    @Override
    public void setMultipart(MultipartDefinition definition) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        definition.getFiles().forEach((k, f) ->
            builder.addFormDataPart(
                f.getParameterName(),
                f.getFilename(),
                RequestBody.create(
                    MediaType.parse(f.getContentType()),
                    f.getBytes()
                )
            )
        );

        definition.getParameters().forEach((k, v) -> builder.addFormDataPart(k, v == null ? "" : String.valueOf(v)));

        body = builder.build();
    }

    @Override
    public void addParameter(String name, Object value) {
        parameters.put(name, value == null ? "" : value.toString());
    }

    Request buildOkHttpRequest() {
        HttpUrl.Builder url;
        if (baseUri != null) {
            String pathSegment = uri != null && uri.startsWith("/") ? uri.substring(1) : uri;
            url = Objects.requireNonNull(HttpUrl.parse(baseUri)).newBuilder().addPathSegments(pathSegment);
        } else {
            url = Objects.requireNonNull(HttpUrl.parse(uri)).newBuilder();
        }

        if (!parameters.isEmpty()) {
            if (TestDefinitionBuilder.HAS_URI_PARAMETERS.contains(method) || body != null) {
                parameters.forEach(url::addQueryParameter);
                builder.method(method, body);
            } else {
                FormBody.Builder form = new FormBody.Builder();
                parameters.forEach(form::add);
                builder.method(method, form.build());
            }
        } else if (HttpMethod.requiresRequestBody(method)) {
            builder.method(method, body != null? body : RequestBody.create(null, ""));
        } else {
            builder.method(method, body);
        }

        return builder.url(url.toString()).build();
    }
}
