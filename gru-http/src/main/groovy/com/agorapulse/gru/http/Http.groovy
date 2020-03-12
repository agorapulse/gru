package com.agorapulse.gru.http

import com.agorapulse.gru.AbstractClient
import com.agorapulse.gru.Client
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import groovy.transform.CompileStatic
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import okhttp3.OkHttpClient
import space.jasan.support.groovy.closure.ConsumerWithDelegate

import java.util.function.Consumer

/**
 * Http Gru client preforms real HTTP calls against given URIs.
 */
@CompileStatic
class Http extends AbstractClient {

    private final OkHttpClient httpClient

    private GruHttpRequest request
    private GruHttpResponse response

    static Http steal(Object unitTest) {
        return new Http(unitTest, null)
    }

    static Http steal(Object unitTest, Consumer<OkHttpClient.Builder> configuration) {
        return new Http(unitTest, configuration)
    }

    static Http steal(
        Object unitTest,
        @DelegatesTo(value = OkHttpClient.Builder, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType, options = 'okhttp3.OkHttpClient.Builder')
            Closure<OkHttpClient.Builder> configuration
    ) {
        return new Http(unitTest, ConsumerWithDelegate.create(configuration))
    }

    private Http(Object unitTest, Consumer<OkHttpClient.Builder> configuration) {
        super(unitTest)
        request = new GruHttpRequest()

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
        if (configuration != null) {
            configuration.accept(builder)
        }
        httpClient = builder.build()
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
        super.unitTest
    }

    @Override
    GruContext run(Squad squad, GruContext context) {
        okhttp3.Response response = httpClient.newCall(request.buildOkHttpRequest()).execute()

        this.response = new GruHttpResponse(response)

        return context.withResult(response)
    }
}
