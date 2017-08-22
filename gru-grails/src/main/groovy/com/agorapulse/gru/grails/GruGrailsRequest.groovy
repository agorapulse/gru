package com.agorapulse.gru.grails

import com.agorapulse.gru.Client
import grails.testing.web.controllers.ControllerUnitTest
import groovy.transform.PackageScope
import org.grails.plugins.testing.GrailsMockHttpServletRequest

/**
 * Wrapper around Grails mock request.
 */
@PackageScope class GruGrailsRequest implements Client.Request {

    private final GrailsMockHttpServletRequest request
    private final ControllerUnitTest unitTest

    GruGrailsRequest(GrailsMockHttpServletRequest request, ControllerUnitTest unitTest) {
        this.request = request
        this.unitTest = unitTest
    }

    @Override
    void setUri(String uri) {
        request.requestURI = uri
    }

    @Override
    String getUri() {
        return request.requestURI
    }

    @Override
    void setMethod(String method) {
        request.method = method
    }

    @Override
    String getMethod() {
        return request.method
    }

    @Override
    void addHeader(String name, String value) {
        request.addHeader(name, value)
    }

    @Override
    void setJson(String jsonText) {
        request.setJson(jsonText)
    }

    @Override
    void addParameter(String name, Object value) {
        unitTest.params.put(name, value)
    }
}
