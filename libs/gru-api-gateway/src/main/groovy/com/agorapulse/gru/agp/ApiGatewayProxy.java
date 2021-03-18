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
package com.agorapulse.gru.agp;

import com.agorapulse.gru.AbstractClient;
import com.agorapulse.gru.Client;
import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FromString;
import space.jasan.support.groovy.closure.ConsumerWithDelegate;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.Consumer;

public class ApiGatewayProxy extends AbstractClient {

    @Deprecated
    public static ApiGatewayProxy steal(Object unitTest, Consumer<ApiGatewayConfiguration> configuration) {
        return create(unitTest, configuration);
    }

    @Deprecated
    public static ApiGatewayProxy steal(Object unitTest,
                                        @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ApiGatewayConfiguration.class)
                                        @ClosureParams(value = FromString.class, options = "com.agorapulse.gru.agp.ApiGatewayConfiguration")
                                        Closure<ApiGatewayConfiguration.Mapping> configuration
    ) {
        return create(configuration);
    }

    public static ApiGatewayProxy create(Object unitTest, Consumer<ApiGatewayConfiguration> configuration) {
        ApiGatewayConfiguration apiGatewayConfiguration = new ApiGatewayConfiguration();
        configuration.accept(apiGatewayConfiguration);
        return new ApiGatewayProxy(unitTest, apiGatewayConfiguration);
    }

    public static ApiGatewayProxy create(
                                        @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ApiGatewayConfiguration.class)
                                        @ClosureParams(value = FromString.class, options = "com.agorapulse.gru.agp.ApiGatewayConfiguration")
                                            Closure<ApiGatewayConfiguration.Mapping> configuration
    ) {
        return create(configuration.getOwner(), ConsumerWithDelegate.create(configuration));
    }

    private final ApiGatewayConfiguration configuration;

    private ApiGatewayProxy(Object unitTest, ApiGatewayConfiguration apiGatewayConfiguration) {
        super(unitTest);
        this.configuration = apiGatewayConfiguration;
    }

    private ApiGatewayProxyRequest request = new ApiGatewayProxyRequest();
    private ApiGatewayProxyResponse response;

    @Override
    public Client.Request getRequest() {
        return request;
    }

    @Override
    public Client.Response getResponse() {
        return Objects.requireNonNull(response, "Response hasn't been set yet");
    }

    @Override
    public void reset() {
        request = new ApiGatewayProxyRequest();
        response = null;
    }


    @Override
    public GruContext run(Squad squad, GruContext context) {
        try {
            this.response = new ApiGatewayProxyResponse(configuration
                .findMapping(request.getMethod(), request.getPath())
                .executeHandler(request, unitTest)
            );
            return context.withResult(this.response);
        } catch (ClassNotFoundException | IOException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            return context.withError(e);
        }
    }
}
