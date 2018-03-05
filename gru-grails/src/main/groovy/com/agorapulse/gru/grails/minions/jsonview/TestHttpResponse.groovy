package com.agorapulse.gru.grails.minions.jsonview

import grails.plugin.json.view.test.JsonRenderResult
import grails.views.api.http.Response
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.springframework.http.HttpStatus


@CompileStatic @PackageScope
class TestHttpResponse implements Response {
    final JsonRenderResult result

    TestHttpResponse(JsonRenderResult result) {
        this.result = result
    }

    @Override
    void header(String name, String value) {
        result.headers[name] = value
    }

    @Override
    void header(Map<String, String> nameAndValue) {
        result.headers.putAll(nameAndValue)
    }

    @Override
    void headers(Map<String, String> namesAndValues) {
        result.headers.putAll(namesAndValues)
    }

    @Override
    void contentType(String contentType) {
        result.contentType = contentType
    }

    @Override
    void encoding(String encoding) {
        // ignore
    }

    @Override
    void status(int status) {
        result.status = HttpStatus.valueOf(status)
    }

    @Override
    void status(int status, String message) {
        result.status = HttpStatus.valueOf(status)
        result.message = message
    }

    @Override
    void status(HttpStatus status) {
        result.status = status
        result.message = status.getReasonPhrase()
    }

    @Override
    void status(HttpStatus status, String message) {
        result.status = status
        result.message = message
    }
}
