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
package com.agorapulse.gru.okhttp;

import com.agorapulse.gru.AbstractClient;
import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.lang.management.ManagementFactory.getRuntimeMXBean;

/**
 * Http Gru client preforms real HTTP calls against given URIs.
 */
public class OkHttp extends AbstractClient {

    private final OkHttpClient httpClient;
    private GruOkHttpRequest request;
    private GruOkHttpResponse response;

    public static OkHttp create(Object unitTest) {
        return new OkHttp(unitTest, null);
    }

    public static OkHttp create(Class<?> unitTestClass) {
        return new OkHttp(unitTestClass, null);
    }

    public static OkHttp create(Object unitTest, Consumer<OkHttpClient.Builder> configuration) {
        return new OkHttp(unitTest, configuration);
    }

    public static OkHttp create(Class<?> unitTestClass, Consumer<OkHttpClient.Builder> configuration) {
        return new OkHttp(unitTestClass, configuration);
    }

    @Deprecated
    public static OkHttp steal(Object unitTest) {
        return create(unitTest);
    }

    @Deprecated
    public static OkHttp steal(Object unitTest, Consumer<OkHttpClient.Builder> configuration) {
        return create(unitTest, configuration);
    }

    private OkHttp(Object unitTest, Consumer<OkHttpClient.Builder> configuration) {
        super(unitTest);
        request = new GruOkHttpRequest();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (configuration != null) {
            configuration.accept(builder);
        }

        httpClient = builder.build();
    }

    private OkHttp(Class<?> unitTestClass, Consumer<OkHttpClient.Builder> configuration) {
        super(unitTestClass);
        request = new GruOkHttpRequest();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (configuration != null) {
            configuration.accept(builder);
        }

        if (isDebugMode()) {
            increaseTimeouts(builder);
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
        request = new GruOkHttpRequest();
        response = null;
    }

    @Override
    public GruContext run(Squad squad, GruContext context) {
        try {
            okhttp3.Response response = httpClient.newCall(request.buildOkHttpRequest()).execute();
            this.response = new GruOkHttpResponse(response);
            return context.withResult(response);
        } catch (IOException e) {
            throw new AssertionError("Failed to execute request " + request, e);
        }
    }

    private void increaseTimeouts(OkHttpClient.Builder builder) {
        builder
            .callTimeout(1, TimeUnit.HOURS)
            .connectTimeout(1, TimeUnit.HOURS)
            .writeTimeout(1, TimeUnit.HOURS)
            .readTimeout(1, TimeUnit.HOURS);
    }

    private boolean isDebugMode() {
        return getRuntimeMXBean().getInputArguments().stream().anyMatch(arg -> arg.contains("-agentlib:jdwp"));
    }

}
