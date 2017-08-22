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
    private final String baseUrl

    String method = TestDefinitionBuilder.GET
    String uri

    GruHttpRequest(String baseUrl) {
        this.baseUrl = baseUrl
    }

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
        HttpUrl.Builder url = HttpUrl.parse(baseUrl).newBuilder().addPathSegments(uri)
        if (parameters) {
            if (method == TestDefinitionBuilder.GET || body) {
                parameters.each { key, value ->
                    url.addQueryParameter(key, value)
                }
                if (!body) {
                    builder.get()
                }
            } else {
                FormBody.Builder form = new FormBody.Builder()
                parameters.each { key, value ->
                    form.add(key, value)
                }
                builder.method(method, form.build())
            }
        }

        if (body) {
            builder.method(method, body)
        }

        builder.url(url.toString()).build()
    }
}
