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
package com.agorapulse.gru.http

import com.agorapulse.gru.Client
import com.agorapulse.gru.MultipartDefinition
import com.agorapulse.gru.TestDefinitionBuilder
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.internal.http.HttpMethod

/**
 * Wrapper around OkHttp request.
 */
@CompileStatic
@PackageScope
class GruHttpRequest implements Client.Request {

    public static final MediaType JSON = MediaType.parse('application/json; charset=utf-8')

    private final Map<String, String> parameters = [:]
    private final Request.Builder builder = new Request.Builder()
    private RequestBody body

    String baseUri
    String method = TestDefinitionBuilder.GET
    String uri

    @Override
    void setUri(String uri) {
        this.uri = uri
    }

    @Override
    void setMethod(String method) {
        this.method = method
    }

    @Override
    void addHeader(String name, String value) {
        builder.addHeader(name, value)
    }

    @Override
    void setJson(String jsonText) {
        body = RequestBody.create(JSON, jsonText)
    }

    @Override
    void setContent(String contentType, byte[] payload) {
        body = RequestBody.create(MediaType.parse(contentType), payload)
    }

    @Override
    void setMultipart(MultipartDefinition definition) {
        MultipartBody.Builder builder = new MultipartBody.Builder()

        definition.files.each { k, f ->
            builder.addFormDataPart(f.parameterName, f.filename, RequestBody.create(
                MediaType.parse(f.contentType),
                f.bytes
            ))
        }

        definition.parameters.each { k, v ->
            builder.addFormDataPart(k, v ? String.valueOf(v) : null)
        }

        body = builder.build()
    }

    @Override
    void addParameter(String name, Object value) {
        parameters.put(name, value == null ? '' : value.toString())
    }

    Request buildOkHttpRequest() {
        HttpUrl.Builder url
        if (baseUri) {
            String pathSegment = uri?.startsWith('/') ? uri[1..-1] : uri
            url = HttpUrl.parse(baseUri).newBuilder().addPathSegments(pathSegment)
        } else {
            url = HttpUrl.parse(uri).newBuilder()
        }

        if (parameters) {
            if (method in TestDefinitionBuilder.HAS_URI_PARAMETERS || body) {
                parameters.each { key, value ->
                    url.addQueryParameter(key, value)
                }
                builder.method(method, body)
            } else {
                FormBody.Builder form = new FormBody.Builder()
                parameters.each { key, value ->
                    form.add(key, value)
                }
                builder.method(method, form.build())
            }
        } else if (HttpMethod.requiresRequestBody(method)) {
            builder.method(method, body ?: RequestBody.create(null, ''))
        } else {
            builder.method(method, body)
        }

        builder.url(url.toString()).build()
    }
}
