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
        header((name): value)
    }

    @Override
    void header(Map<String, String> nameAndValue) {
        headers(nameAndValue)
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
        this.status(HttpStatus.valueOf(status))
    }

    @Override
    void status(int status, String message) {
        this.status(HttpStatus.valueOf(status), message)
    }

    @Override
    void status(HttpStatus status) {
        this.status(status, status.reasonPhrase)
    }

    @Override
    void status(HttpStatus status, String message) {
        result.status = status
        result.message = message
    }
}
