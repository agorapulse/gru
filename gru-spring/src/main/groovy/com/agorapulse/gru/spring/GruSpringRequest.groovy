package com.agorapulse.gru.spring

import com.agorapulse.gru.Client
import com.agorapulse.gru.TestDefinitionBuilder
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

/**
 * Gru wrapper around Spring mock request.
 */
@PackageScope
@CompileStatic
class GruSpringRequest implements Client.Request {

    final List<Closure<MockHttpServletRequestBuilder>> steps = []

    String baseUri
    String method = TestDefinitionBuilder.GET
    String uri

    @Override
    void addHeader(String name, String value) {
        addBuildStep { header(name, value) }
    }

    @Override
    void setJson(String jsonText) {
        addBuildStep { contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonText) }
    }

    @Override
    void addParameter(String name, Object value) {
        addBuildStep { param(name, value?.toString()) }
    }

    void addBuildStep(@DelegatesTo(MockHttpServletRequestBuilder) Closure<MockHttpServletRequestBuilder> step) {
        steps << step
    }
}
