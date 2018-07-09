package com.agorapulse.gru.agp;

import com.agorapulse.gru.Client;
import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;

import java.util.Collections;
import java.util.LinkedHashMap;
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
    public void addParameter(String name, Object value) {
        if (queryStringParameters == null) {
            queryStringParameters = new LinkedHashMap<>();
        }
        queryStringParameters.put(name, value == null ? null : value.toString());
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

        }

        if (httpMethod != null) {
            output.put("httpMethod", httpMethod);

        }

        if (queryStringParameters != null) {
            output.put("queryStringParameters", queryStringParameters);
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
