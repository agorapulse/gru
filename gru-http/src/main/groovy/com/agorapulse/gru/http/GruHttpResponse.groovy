package com.agorapulse.gru.http

import com.agorapulse.gru.Client
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import okhttp3.Response

/**
 * Wrapper around OkHttp response.
 */
@CompileStatic
@PackageScope
class GruHttpResponse implements Client.Response {

    private final Response response

    GruHttpResponse(Response response) {
        this.response = response
    }

    @Override
    int getStatus() {
        response.code()
    }

    @Override
    List<String> getHeaders(String name) {
        return response.headers(name)
    }

    @Override
    String getText() {
        return response.body().string()
    }

    @Override
    String getRedirectUrl() {
        return response.header('Location')
    }
}
