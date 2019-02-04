package com.agorapulse.gru.grails

import com.agorapulse.gru.Client
import org.grails.plugins.testing.GrailsMockHttpServletResponse

/**
 * Wrapper around mock Grails response.
 */
class GruGrailsResponse implements Client.Response {

    final GrailsMockHttpServletResponse response

    GruGrailsResponse(GrailsMockHttpServletResponse response) {
        this.response = response
    }

    @Override
    int getStatus() {
        return response.status
    }

    @Override
    List<String> getHeaders(String name) {
        return response.headers(name)
    }

    @Override
    String getText() {
        return response.text
    }

    @Override
    String getRedirectUrl() {
        return response.redirectUrl
    }
}
