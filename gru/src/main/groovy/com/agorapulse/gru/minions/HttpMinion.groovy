package com.agorapulse.gru.minions

import com.agorapulse.gru.Client
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.google.common.collect.LinkedListMultimap
import com.google.common.collect.Multimap
import groovy.transform.CompileStatic

/**
 * Minion responsible for HTTP status, uri, method, headers and redirection and forwarding.
 */
@CompileStatic
class HttpMinion extends AbstractMinion<Client> {

    HttpMinion() {
        super(Client)
    }

    @Override
    GruContext doBeforeRun(Client client, Squad squad, GruContext context) {
        if (requestHeaders.size() > 0) {
            for (Map.Entry<String, String> header : requestHeaders.entries()) {
                client.request.addHeader(header.key, header.value)
            }
        }

        return context
    }

    @Override
    void doVerify(Client client, Squad squad, GruContext context) throws Throwable {
        assert client.response.status == status

        for (Map.Entry<String, String> header : responseHeaders.entries()) {
            assert client.response.getHeaders(header.key).contains(header.value)
        }

        assert redirectUri == null ||
            client.response.redirectUrl == redirectUri ||
            client.response.redirectUrl == client.request.baseUri + redirectUri
    }

    void setStatus(int status) {
        this.status = status
    }

    Multimap<String, String> getRequestHeaders() {
        return requestHeaders
    }

    Multimap<String, String> getResponseHeaders() {
        return responseHeaders
    }

    void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri
    }

    public static final int DEFAULT_STATUS = 200
    final int index = HTTP_MINION_INDEX

    private int status = DEFAULT_STATUS
    private final Multimap<String, String> requestHeaders = LinkedListMultimap.create()
    private final Multimap<String, String> responseHeaders = LinkedListMultimap.create()
    private String redirectUri

}
