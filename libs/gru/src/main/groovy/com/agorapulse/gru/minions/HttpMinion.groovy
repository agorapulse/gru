/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2021 Agorapulse.
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
package com.agorapulse.gru.minions

import com.agorapulse.gru.Client
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
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
            for (Map.Entry<String, Collection<String>> header : requestHeaders.entrySet()) {
                Optional.ofNullable(header.getValue()).ifPresent {
                    it.each {
                        client.request.addHeader(header.key, it)
                    }
                }
            }
        }

        return context
    }

    @Override
    void doVerify(Client client, Squad squad, GruContext context) throws Throwable {
        assert client.response.status == status

        for (Map.Entry<String, Collection<String>> header : responseHeaders.entrySet()) {
            Optional.ofNullable(header.getValue()).ifPresent {
                it.each {
                    assert client.response.getHeaders(header.key).contains(it)
                }
            }
        }

        assert redirectUri == null ||
            client.response.redirectUrl == redirectUri ||
            client.response.redirectUrl == client.request.baseUri + redirectUri
    }

    void setStatus(int status) {
        this.status = status
    }

    Map<String, Collection<String>> getRequestHeaders() {
        return requestHeaders
    }

    Map<String, Collection<String>> getResponseHeaders() {
        return responseHeaders
    }

    void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri
    }

    public static final int DEFAULT_STATUS = 200
    final int index = HTTP_MINION_INDEX

    private int status = DEFAULT_STATUS
    private final Map<String, Collection<String>> requestHeaders = new LinkedHashMap<>()
    private final Map<String, Collection<String>> responseHeaders = new LinkedHashMap<>()
    private String redirectUri

}
