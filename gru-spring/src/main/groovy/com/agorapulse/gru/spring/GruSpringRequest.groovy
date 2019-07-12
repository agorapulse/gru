package com.agorapulse.gru.spring

import com.agorapulse.gru.Client
import com.agorapulse.gru.MultipartDefinition
import com.agorapulse.gru.TestDefinitionBuilder
import com.agorapulse.gru.cookie.Cookie
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import space.jasan.support.groovy.closure.ConsumerWithDelegate

import java.util.function.Consumer

/**
 * Gru wrapper around Spring mock request.
 */
@PackageScope
@CompileStatic
class GruSpringRequest implements Client.Request {

    final List<Consumer<MockHttpServletRequestBuilder>> steps = []

    String baseUri
    String method = TestDefinitionBuilder.GET
    String uri
    MultipartDefinition multipart

    @Override
    void addHeader(String name, String value) {
        addBuildStep { header(name, value) }
    }

    @Override
    void setJson(String jsonText) {
        addBuildStep { contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonText) }
    }

    @Override
    void setContent(String mediaType, byte[] payload) {
        addBuildStep { contentType(MediaType.parseMediaType(mediaType)).content(payload) }
    }

    @Override
    void addParameter(String name, Object value) {
        addBuildStep { param(name, value?.toString()) }
    }

    void addBuildStep(Consumer<MockHttpServletRequestBuilder> step) {
        steps << step
    }

    void addBuildStep(
        @DelegatesTo(value = MockHttpServletRequestBuilder, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType, options = 'org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder')
            Closure<MockHttpServletRequestBuilder> step
    ) {
        addBuildStep(ConsumerWithDelegate.create(step))
    }

    @Override
    void setMultipart(MultipartDefinition definition) {
        this.multipart = definition
    }

    @Override
    void addCookie(Cookie c) {
        addBuildStep {
            cookie new javax.servlet.http.Cookie(c.name, c.value)
        }
    }
}
