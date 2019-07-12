package com.agorapulse.gru.spring

import com.agorapulse.gru.Client
import com.agorapulse.gru.cookie.Cookie
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

    @Override
    @SuppressWarnings('ExplicitArrayListInstantiation')
    List<Cookie> getCookies() {
        List<javax.servlet.http.Cookie> cookies = response.cookies ? response.cookies.toList() : new ArrayList<javax.servlet.http.Cookie>()

        return cookies.collect {
            Cookie.Builder builder = new Cookie.Builder()
                .name(it.name)
                .value(it.value)

            // TODO: expires?

            if (it.domain) {
                builder.domain(it.domain)
            }

            builder
                .httpOnly(it.httpOnly)
                .path(it.path)
                .secure(it.secure)
                .build()
        }
    }
}
