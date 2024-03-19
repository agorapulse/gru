/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2024 Agorapulse.
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
package com.agorapulse.gru.http;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.MultipartDefinition;
import com.agorapulse.gru.TestDefinitionBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Wrapper around OkHttp request.
 */
class GruHttpRequest implements Client.Request {

    private final Map<String, String> parameters = new LinkedHashMap<>();
    private final HttpRequest.Builder builder = HttpRequest.newBuilder();
    private HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.noBody();

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
        builder.header(name, value);
    }

    @Override
    public void setJson(String jsonText) {
        addHeader("Content-Type", "application/json");
        body = HttpRequest.BodyPublishers.ofString(jsonText);
    }

    @Override
    public void setContent(String contentType, byte[] payload) {
        addHeader("Content-Type", contentType);
        body = HttpRequest.BodyPublishers.ofByteArray(payload);
    }

    @Override
    public void setMultipart(MultipartDefinition definition) {
        String boundary = Long.toHexString(System.currentTimeMillis());
        builder.header("Content-Type", "multipart/form-data; boundary=" + boundary);
        body = ofMultipartData(definition, boundary);
    }

    @Override
    public void addParameter(String name, Object value) {
        parameters.put(name, value == null ? "" : value.toString());
    }

    HttpRequest buildHttpRequest() {
        if (!parameters.isEmpty()) {
            if (TestDefinitionBuilder.HAS_URI_PARAMETERS.contains(method) || body != null) {
                builder.uri(buildUri(baseUri, uri, parameters));
                builder.method(method, body);
            } else {
                StringBuilder params = new StringBuilder();
                appendParameters(parameters, params);
                builder.uri(buildUri(baseUri, uri, Collections.emptyMap()));
                builder.header("Content-Type", "application/x-www-form-urlencoded");
                builder.method(method, HttpRequest.BodyPublishers.ofString(params.toString()));
            }
        } else if (TestDefinitionBuilder.REQUIRES_BODY.contains(method)) {
            builder.uri(buildUri(baseUri, uri, Collections.emptyMap()));
            builder.method(method, body);
        } else {
            builder.uri(buildUri(baseUri, uri, Collections.emptyMap()));
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        return builder.build();
    }

private static HttpRequest.BodyPublisher ofMultipartData(MultipartDefinition parts, String boundary) {
    List<byte[]> byteArrays = new ArrayList<>();
    String separator = "--" + boundary + "\r\nContent-Disposition: form-data; name=";
    String end = "\r\n";

    for (Map.Entry<String, Object> part : parts.getParameters().entrySet()) {
        byteArrays.add(separator.getBytes(StandardCharsets.UTF_8));
        byteArrays.add(("\"" + part.getKey() + "\"\r\n\r\n" + part.getValue() + end).getBytes(StandardCharsets.UTF_8));
    }

    for (Map.Entry<String, MultipartDefinition.MultipartFile> part : parts.getFiles().entrySet()) {
        byteArrays.add(separator.getBytes(StandardCharsets.UTF_8));
        byteArrays.add(("\"" + part.getValue().getParameterName() + "\"; filename=\"" + part.getValue().getFilename() + "\"\r\nContent-Type: " + part.getValue().getContentType() + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        byteArrays.add(part.getValue().getBytes());
        byteArrays.add(end.getBytes(StandardCharsets.UTF_8)); // End of the file content
    }

    byteArrays.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8)); // Final boundary
    return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
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
