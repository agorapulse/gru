package com.agorapulse.gru.agp;

import com.agorapulse.gru.Client;
import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

class ApiGatewayProxyResponse implements Client.Response {

    private String text;
    private Map<String, String> headers;
    private int status;

    // TODO: base64 encoded

    @SuppressWarnings("unchecked")
    ApiGatewayProxyResponse(String responseText) {
        Map response = (Map) new JsonSlurper().parseText(responseText);
        Object body = response.get("body");
        if (body != null) {
            this.text = body instanceof String ? body.toString() : JsonOutput.toJson(body);
        }

        this.headers = (Map<String, String>) response.getOrDefault("headers", null);

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
        if (headers.containsKey(name)) {
            return Collections.singletonList(headers.get(name));
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
