package com.agorapulse.gru.spring

import com.agorapulse.gru.Client
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.springframework.mock.web.MockHttpServletResponse

/**
 * Gru wrapper around Spring mock response.
 */
@PackageScope
@CompileStatic
class GruSpringResponse implements Client.Response {

    private final MockHttpServletResponse response

    GruSpringResponse(MockHttpServletResponse response) {
        this.response = response
    }

    @Override
    int getStatus() {
        response.status
    }

    @Override
    List<String> getHeaders(String name) {
        return response.getHeaders(name)
    }

    @Override
    String getText() {
        return response.contentAsString
    }

    @Override
    String getRedirectUrl() {
        return response.redirectedUrl
    }
}
