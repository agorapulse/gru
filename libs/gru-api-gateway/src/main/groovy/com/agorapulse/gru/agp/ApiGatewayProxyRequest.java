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
package com.agorapulse.gru.agp;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.MultipartDefinition;
import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class ApiGatewayProxyRequest implements Client.Request {

    private String body;
    private Map<String, String> headers;
    private String httpMethod;
    private Map<String, String> queryStringParameters;
    private Map<String, String> pathParameters;

    private MockContext context = new MockContext();

    // TODO: stage variables
    // TODO: request context
    // TODO: base 64 encoding
    // TODO: resource

    private String baseUri;
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
        return uri;
    }

    @Override
    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String getMethod() {
        return httpMethod;
    }

    @Override
    public void setMethod(String method) {
        httpMethod = method;
    }

    @Override
    public void addHeader(String name, String value) {
        if (headers == null) {
            headers = new LinkedHashMap<>();
        }
        headers.put(name, value);
    }

    @Override
    public void setJson(String jsonText) {
        body = jsonText;
    }

    @Override
    public void setContent(String contentType, byte[] payload) {
        addHeader("Content-Type", contentType);
        body = new String(payload);
    }

    @Override
    public void addParameter(String name, Object value) {
        if (queryStringParameters == null) {
            queryStringParameters = new LinkedHashMap<>();
        }
        queryStringParameters.put(name, value == null ? null : value.toString());
    }

    @Override
    public void setMultipart(MultipartDefinition definition) {
        throw new UnsupportedOperationException("File uploads are currently not supported for API Gateway!");
    }

    public void setPathParameters(Map<String, String> pathParameters) {
        this.pathParameters = Collections.unmodifiableMap(pathParameters);
    }

    String getPath() {
        if (baseUri == null) {
            return uri;
        }
        return (baseUri + uri).replace("//", "/");
    }

    public MockContext getContext() {
        return this.context;
    }

    public String toJson() {
        Map<String, Object> output = new LinkedHashMap<>();

        output.put("path", getPath());

        if (body != null) {
            output.put("body", body);
        }

        if (headers != null) {
            output.put("headers", headers);
            Map<String, List<String>> multiValueHeaders = new LinkedHashMap<>();
            headers.forEach((k, v) -> multiValueHeaders.put(k, Collections.singletonList(v)));
            output.put("multiValueHeaders", multiValueHeaders);

        }

        if (httpMethod != null) {
            output.put("httpMethod", httpMethod);

        }

        if (queryStringParameters != null) {
            output.put("queryStringParameters", queryStringParameters);
            Map<String, List<String>> multiValueQueryStringParameters = new LinkedHashMap<>();
            queryStringParameters.forEach((k, v) -> multiValueQueryStringParameters.put(k, Collections.singletonList(v)));
            output.put("multiValueQueryStringParameters", multiValueQueryStringParameters);
        }

        if (pathParameters != null) {
            output.put("pathParameters", pathParameters);
        }

        return JsonOutput.toJson(output);
    }

    public String toJson(ApiGatewayConfiguration.MappingConfiguration configuration) {
        final Map<String, Object> output = new LinkedHashMap<>();

        if (pathParameters != null && !configuration.getPathParameters().isEmpty()) {
            configuration.getPathParameters().forEach(parameter -> output.put(parameter, pathParameters.get(parameter)));
        }

        if (queryStringParameters != null && !configuration.getQueryStringParameters().isEmpty()) {
            configuration.getQueryStringParameters().forEach(parameter -> output.put(parameter, queryStringParameters.get(parameter)));
        }

        if (body != null) {
            output.putAll((Map) new JsonSlurper().parseText(body));
        }

        return JsonOutput.toJson(output);
    }
}
