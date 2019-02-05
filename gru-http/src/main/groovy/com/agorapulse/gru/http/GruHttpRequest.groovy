package com.agorapulse.gru.http

import com.agorapulse.gru.Client
import com.agorapulse.gru.TestDefinitionBuilder
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.MediaType
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
