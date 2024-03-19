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
package com.agorapulse.gru.agp.groovy;

import com.agorapulse.gru.agp.ApiGatewayConfiguration;
import com.agorapulse.gru.agp.ApiGatewayProxy;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FromString;
import space.jasan.support.groovy.closure.ConsumerWithDelegate;

public class ApiGatewayProxyStaticExtensions {

    public static ApiGatewayProxy create(
        ApiGatewayProxy self,
        @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ApiGatewayConfiguration.class)
        @ClosureParams(value = FromString.class, options = "com.agorapulse.gru.agp.ApiGatewayConfiguration")
        Closure<ApiGatewayConfiguration.Mapping> configuration
    ) {
        return ApiGatewayProxy.create(configuration.getOwner(), ConsumerWithDelegate.create(configuration));
    }

}
