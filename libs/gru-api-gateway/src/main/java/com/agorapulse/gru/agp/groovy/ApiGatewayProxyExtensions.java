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
package com.agorapulse.gru.agp.groovy;

import com.agorapulse.gru.agp.ApiGatewayConfiguration;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import space.jasan.support.groovy.closure.ConsumerWithDelegate;

public class ApiGatewayProxyExtensions {

    public static ApiGatewayConfiguration.MappingConfiguration response(
        ApiGatewayConfiguration.MappingConfiguration self,
        int number,
        @DelegatesTo(value = ApiGatewayConfiguration.ResponseMappingConfiguration.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.agp.ApiGatewayConfiguration.ResponseMappingConfiguration")
        Closure<ApiGatewayConfiguration.ResponseMappingConfiguration> configuration
    ) {
        return self.response(number, ConsumerWithDelegate.create(configuration));
    }
    public static ApiGatewayConfiguration.Mapping to(
        ApiGatewayConfiguration.Route self,
        Class<?> handler,
        @DelegatesTo(value = ApiGatewayConfiguration.MappingConfiguration.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.agp.ApiGatewayConfiguration.MappingConfiguration")
        Closure<ApiGatewayConfiguration.MappingConfiguration> configuration
    ) {
        return self.to(handler, ConsumerWithDelegate.create(configuration));
    }

    public static ApiGatewayConfiguration.Mapping to(
        ApiGatewayConfiguration.Route self,
        String handler,
        @DelegatesTo(value = ApiGatewayConfiguration.MappingConfiguration.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.agp.ApiGatewayConfiguration.MappingConfiguration")
        Closure<ApiGatewayConfiguration.MappingConfiguration> configuration
    ) {
        return self.to(handler, ConsumerWithDelegate.create(configuration), true);
    }

}
