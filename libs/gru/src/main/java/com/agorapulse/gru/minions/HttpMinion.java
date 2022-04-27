/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2022 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agorapulse.gru.minions;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Minion responsible for HTTP status, uri, method, headers and redirection and forwarding.
 */
public class HttpMinion extends AbstractMinion<Client> {

    public static final int DEFAULT_STATUS = 200;

    private Set<Integer> statuses = Collections.singleton(DEFAULT_STATUS);
    private final Map<String, Collection<String>> requestHeaders = new LinkedHashMap<>();
    private final Map<String, Collection<String>> responseHeaders = new LinkedHashMap<>();
    private String redirectUri;

    public HttpMinion() {
        super(Client.class);
    }

    @Override
    public GruContext doBeforeRun(Client client, Squad squad, GruContext context) {
        if (requestHeaders.size() > 0) {
            for (Map.Entry<String, Collection<String>> header : requestHeaders.entrySet()) {
                Optional.ofNullable(header.getValue()).ifPresent(headers -> {
                    for (String value : headers) {
                        client.getRequest().addHeader(header.getKey(), value);
                    }
                });
            }
        }

        return context;
    }

    @Override
    public void doVerify(Client client, Squad squad, GruContext context) {
        int status = client.getResponse().getStatus();
        if (!statuses.contains(status)) {
            throw new AssertionError("Status " + status + " is not expected. Expected statuses are " + statuses.stream().map(Object::toString).collect(Collectors.joining(", ")));
        }

        for (Map.Entry<String, Collection<String>> header : responseHeaders.entrySet()) {
            Optional.ofNullable(header.getValue()).ifPresent(headers -> {
                for (String value : headers) {
                    if (!(client.getResponse().getHeaders(header.getKey()).contains(value))) {
                        throw new AssertionError("Missing header " + header.getKey() + " with value " + value);
                    }
                }
            });
        }

        if (redirectUri != null) {
            String actualRedirectUrl = client.getResponse().getRedirectUrl();
            if (!(actualRedirectUrl.equals(redirectUri) || actualRedirectUrl.equals(client.getRequest().getBaseUri() + redirectUri))) {
                throw new AssertionError("Unexpected redirect URL " + actualRedirectUrl + ". Expected URL " + redirectUri);
            }
        }
    }

    public void setStatus(int status) {
        this.statuses = Collections.singleton(status);
    }

    public void setStatuses(int... statuses) {
        this.statuses = new TreeSet<>();
        for (int status : statuses) {
            this.statuses.add(status);
        }
    }

    public Map<String, Collection<String>> getRequestHeaders() {
        return requestHeaders;
    }

    public Map<String, Collection<String>> getResponseHeaders() {
        return responseHeaders;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    @Override
    public int getIndex() {
        return HTTP_MINION_INDEX;
    }

}
