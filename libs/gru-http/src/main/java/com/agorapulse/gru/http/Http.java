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
package com.agorapulse.gru.http;

import com.agorapulse.gru.AbstractClient;
import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Http Gru client preforms real HTTP calls against given URIs.
 */
public class Http extends AbstractClient {

    private final OkHttpClient httpClient;
    private GruHttpRequest request;
    private GruHttpResponse response;

    public static Http create(Object unitTest) {
        return new Http(unitTest, null);
    }

    public static Http create(Object unitTest, Consumer<OkHttpClient.Builder> configuration) {
        return new Http(unitTest, configuration);
    }

    @Deprecated
    public static Http steal(Object unitTest) {
        return create(unitTest);
    }

    @Deprecated
    public static Http steal(Object unitTest, Consumer<OkHttpClient.Builder> configuration) {
        return create(unitTest, configuration);
    }

    private Http(Object unitTest, Consumer<OkHttpClient.Builder> configuration) {
        super(unitTest);
        request = new GruHttpRequest();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (configuration != null) {
            configuration.accept(builder);
        }

        httpClient = builder.build();
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
        request = new GruHttpRequest();
        response = null;
    }

    @Override
    public GruContext run(Squad squad, GruContext context) {
        try {
            okhttp3.Response response = httpClient.newCall(request.buildOkHttpRequest()).execute();
            this.response = new GruHttpResponse(response);
            return context.withResult(response);
        } catch (IOException e) {
            throw new AssertionError("Failed to execute request " + request, e);
        }
    }

}
