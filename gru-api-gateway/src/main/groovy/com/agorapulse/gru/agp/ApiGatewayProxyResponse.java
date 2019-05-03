package com.agorapulse.gru.agp;

import com.agorapulse.gru.Client;
import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;

import java.util.*;

class ApiGatewayProxyResponse implements Client.Response {

    private String text;
    private Map<String, List<String>> multiValueHeaders;
    private int status;

    // TODO: base64 encoded

    @SuppressWarnings("unchecked")
    ApiGatewayProxyResponse(String responseText) {
        Map response = (Map) new JsonSlurper().parseText(responseText);
        Object body = response.get("body");
        if (body != null) {
            this.text = body instanceof String ? body.toString() : JsonOutput.toJson(body);
        }

        multiValueHeaders = (Map<String, List<String>>) response.getOrDefault("multiValueHeaders", new LinkedHashMap<>());

        if (response.containsKey("headers")) {
            Map<String, String> headers = (Map<String, String>) response.get("headers");
            headers.forEach((k, v) -> {
                List<String> values = multiValueHeaders.computeIfAbsent(k, (key) -> new ArrayList<>());
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
