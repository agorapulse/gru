/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2023 Agorapulse.
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
package com.agorapulse.gru.micronaut.http;

import com.agorapulse.gru.AbstractClient;
import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;

/**
 * Http Gru client preforms real HTTP calls against given URIs.
 */
public class MicronautHttpClient extends AbstractClient {

    public static MicronautHttpClient create(Object unitTest, HttpClient client) {
        return new MicronautHttpClient(unitTest, client);
    }

    public static MicronautHttpClient create(Class<?> unitTestClass, HttpClient client) {
        return new MicronautHttpClient(unitTestClass, client);
    }

    private final HttpClient client;

    private MicronautHttpRequest request;
    private MicronautHttpResponse response;

    private MicronautHttpClient(Object unitTest, HttpClient client) {
        super(unitTest);
        this.client = client;
        reset();
    }

    private MicronautHttpClient(Class<?> unitTestClass, HttpClient client) {
        super(unitTestClass);
        this.client = client;
        reset();
    }

    @Override
    public Request getRequest() {
        return request;
    }

    @Override
    public Response getResponse() {
        if (response == null) {
            throw new IllegalStateException("Response hasn't been set yet");
        }

        return response;
    }

    @Override
    public void reset() {
        request = new MicronautHttpRequest();
        response = null;
    }

    @Override
    public GruContext run(Squad squad, GruContext context) {
        HttpResponse<?> exchange = client.toBlocking().exchange(request.buildHttpRequest(), String.class);

        response = new MicronautHttpResponse(exchange);
        return context.withResult(response);
    }

}
