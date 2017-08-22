package com.agorapulse.gru.http

import com.agorapulse.gru.AbstractClient
import com.agorapulse.gru.Client
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.minions.HttpMinion
import groovy.transform.CompileStatic
import okhttp3.OkHttpClient

/**
 * Http Gru client preforms real HTTP calls against given URIs.
 */
@CompileStatic
class Http extends AbstractClient {

    private final OkHttpClient httpClient = new OkHttpClient()

    private GruHttpRequest request
    private GruHttpResponse response

    static Http steal(Object unitTest) {
        return new Http(unitTest)
    }

    private Http(Object unitTest) {
        super(unitTest)
        request = new GruHttpRequest()
    }

    @Override
    Client.Request getRequest() {
        return request
    }

    @Override
    Client.Response getResponse() {
        if (!response) {
            throw new IllegalStateException("Response hasn't been set yet")
        }
        return response
    }

    @Override
    void reset() {
        request = new GruHttpRequest()
        response = null
    }

    @Override
    Object getUnitTest() {
        return this.@unitTest
    }

    @Override
    GruContext run(Squad squad, GruContext context) {
        okhttp3.Response response = httpClient.newCall(request.buildOkHttpRequest()).execute()

        this.response = new GruHttpResponse(response)

        return context.withResult(response)
    }
}
