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
package com.agorapulse.gru.micronaut;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import io.micronaut.context.ApplicationContextBuilder;
import space.jasan.support.groovy.closure.ConsumerWithDelegate;

public class MicronautGruStaticExtensions {

    /**
     * Creates new client builder and configures the {@link ApplicationContextBuilder}.
     * @param configuration application context builder configuration
     * @return new custom application context builder
     */
    @SuppressWarnings("unused")
    public static Micronaut.MicronautApplicationBuilder build(
        Micronaut self,
        Object unitTest,
        @DelegatesTo(value = ApplicationContextBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "io.micronaut.context.ApplicationContextBuilder")
            Closure<ApplicationContextBuilder> configuration
    ) {
        return Micronaut.build(unitTest).doWithContextBuilder(ConsumerWithDelegate.create(configuration));
    }

}
