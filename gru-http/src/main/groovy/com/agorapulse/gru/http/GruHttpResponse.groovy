package com.agorapulse.gru.http

import com.agorapulse.gru.Client
import com.agorapulse.gru.cookie.Cookie
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
        if (response.priorResponse()?.redirect) {
            return response.priorResponse().code()
        }
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
        return response.priorResponse()?.header('Location')
    }
}
