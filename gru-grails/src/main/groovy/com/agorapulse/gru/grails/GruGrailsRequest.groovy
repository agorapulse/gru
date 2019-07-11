package com.agorapulse.gru.grails

import com.agorapulse.gru.Client
import com.agorapulse.gru.MultipartDefinition
import com.agorapulse.gru.cookie.Cookie
import grails.testing.web.controllers.ControllerUnitTest
import org.grails.plugins.testing.GrailsMockHttpServletRequest
import org.springframework.mock.web.MockMultipartFile

/**
 * Wrapper around Grails mock request.
 */
class GruGrailsRequest implements Client.Request {

    private final GrailsMockHttpServletRequest request
    private final ControllerUnitTest unitTest

    String baseUri = ''
    String uri = ''

    GruGrailsRequest(GrailsMockHttpServletRequest request, ControllerUnitTest unitTest) {
        this.request = request
        this.unitTest = unitTest
    }

    @Override
    void setUri(String uri) {
        this.uri = uri
        updateRequestUri()
    }

    void setBaseUri(String uri) {
        this.baseUri = uri
        updateRequestUri()
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
    void addCookie(Cookie cookie) {
        List<javax.servlet.http.Cookie> cookies = request.cookies ? request.cookies.toList() : []

        // request cookies only contain name and value
        javax.servlet.http.Cookie servletCookie = new javax.servlet.http.Cookie(cookie.name, cookie.value)

        cookies.add(servletCookie)

        request.cookies = cookies.toArray(new javax.servlet.http.Cookie[cookies.size()])
    }

    @Override
    void setJson(String jsonText) {
        request.json = jsonText
    }

    @Override
    void setContent(String contentType, byte[] content) {
        request.content = content
        request.contentType = contentType
    }

    @Override
    void addParameter(String name, Object value) {
        unitTest.params.put(name, value)
    }

    @Override
    void setMultipart(MultipartDefinition definition) {
        definition.parameters.each { k, v ->
            unitTest.params.put(k, v ? String.valueOf(v) : null)
        }

        definition.files.each { k, f ->
            request.addFile(new MockMultipartFile(
                f.parameterName,
                f.filename,
                f.contentType,
                f.bytes
            ))
        }
    }

    private void updateRequestUri() {
        unitTest.request.requestURI = "$baseUri$uri".replaceAll('/+', '/')
    }
}
