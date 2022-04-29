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
package com.agorapulse.gru.agp;

import com.agorapulse.gru.Client;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class ApiGatewayProxyResponse implements Client.Response {

    private String text;
    private final Map<String, List<String>> multiValueHeaders;
    private final int status;

    // TODO: base64 encoded

    @SuppressWarnings("unchecked")
    ApiGatewayProxyResponse(String responseText) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> response = mapper.readValue(responseText, mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class));
            Object body = response.get("body");
            if (body != null) {
                this.text = body instanceof String ? body.toString() : mapper.writeValueAsString(body);
            }

            multiValueHeaders = (Map<String, List<String>>) response.getOrDefault("multiValueHeaders", new LinkedHashMap<>());

            if (response.containsKey("headers")) {
                Map<String, String> headers = (Map<String, String>) response.get("headers");
                headers.forEach((k, v) -> {
                    List<String> values = multiValueHeaders.computeIfAbsent(k, key -> new ArrayList<>());
                    if (!values.contains(v)) {
                        values.add(v);
                    }
                });
            }

            if (response.get("statusCode") == null) {
                this.status = 200;
            } else {
                this.status = ((Number)response.get("statusCode")).intValue();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public List<String> getHeaders(String name) {
        if (multiValueHeaders.containsKey(name)) {
            return multiValueHeaders.get(name);
        }
        return Collections.emptyList();
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getRedirectUrl() {
        if (getStatus() == 301 || getStatus() == 302) {
            List<String> locations = getHeaders("Location");
            if (!locations.isEmpty()) {
                return locations.get(0);
            }
        }
        return null;
    }
}
