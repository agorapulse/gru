/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2024 Agorapulse.
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

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

import static java.lang.management.ManagementFactory.getRuntimeMXBean;

/**
 * Http Gru client preforms real HTTP calls against given URIs.
 */
public class Http extends AbstractClient {

    public static Http create(Object unitTest) {
        return new Http(unitTest, null);
    }

    public static Http create(Class<?> unitTestClass) {
        return new Http(unitTestClass, null);
    }

    public static Http create(Object unitTest, Consumer<HttpClient.Builder> configuration) {
        return new Http(unitTest, configuration);
    }

    public static Http create(Class<?> unitTestClass, Consumer<HttpClient.Builder> configuration) {
        return new Http(unitTestClass, configuration);
    }

    private final Consumer<HttpClient.Builder> configuration;

    private GruHttpRequest request;
    private GruHttpResponse response;

    private Http(Object unitTest, Consumer<HttpClient.Builder> configuration) {
        super(unitTest);
        this.configuration = configuration;
        reset();
    }

    private Http(Class<?> unitTestClass, Consumer<HttpClient.Builder> configuration) {
        super(unitTestClass);
        this.configuration = configuration;
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
        request = new GruHttpRequest();
        response = null;
    }

    @Override
    public GruContext run(Squad squad, GruContext context) {
        try {
            HttpClient.Builder builder = HttpClient.newBuilder();

            if (configuration != null) {
                configuration.accept(builder);
            }

            if (isDebugMode()) {
                increaseTimeouts(builder);
            }

            HttpResponse<String> response = builder.build().send(request.buildHttpRequest(), HttpResponse.BodyHandlers.ofString());
            this.response = new GruHttpResponse(response);
            return context.withResult(response);
        } catch (IOException e) {
            throw new AssertionError("Failed to execute request " + request, e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void increaseTimeouts(HttpClient.Builder builder) {
        builder.connectTimeout(Duration.of(1, ChronoUnit.HOURS));
    }

    private boolean isDebugMode() {
        return getRuntimeMXBean().getInputArguments().stream().anyMatch(arg -> arg.contains("-agentlib:jdwp"));
    }

}
