package com.agorapulse.gru.minions;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import groovy.transform.CompileStatic;

import java.util.Map;

/**
 * Minion responsible for HTTP status, uri, method, headers and redirection and forwarding.
 */
@CompileStatic
public class HttpMinion extends AbstractMinion<Client> {

    public HttpMinion() {
        super(Client.class);
    }

    @Override
    public GruContext doBeforeRun(Client client, Squad squad, GruContext context) {
        if (requestHeaders.size() > 0) {
            for (Map.Entry<String, String> header : requestHeaders.entries()) {
                client.getRequest().addHeader(header.getKey(), header.getValue());
            }
        }

        return context;
    }

    @Override
    public void doVerify(Client client, Squad squad, GruContext context) throws AssertionError {
        assert client.getResponse().getStatus() == status;

        for (Map.Entry<String, String> header : responseHeaders.entries()) {
            assert client.getResponse().getHeaders(header.getKey()).contains(header.getValue());
        }

        assert redirectUri == null || client.getResponse().getRedirectUrl().equals(redirectUri);

    }

    public final int getIndex() {
        return HTTP_MINION_INDEX;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Multimap<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Multimap<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public Multimap<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Multimap<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public static final int DEFAULT_STATUS = 200;

    private int status = DEFAULT_STATUS;
    private Multimap<String, String> requestHeaders = LinkedListMultimap.create();
    private Multimap<String, String> responseHeaders = LinkedListMultimap.create();
    private String redirectUri;
}
