/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2025 Agorapulse.
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
import io.micronaut.context.ApplicationContext;
import space.jasan.support.groovy.closure.ConsumerWithDelegate;

public class MicronautGruExtensions {

    /**
     * Configures the {@link ApplicationContext}.
     * @param configuration application context configuration
     * @return self
     */
    public static Micronaut.MicronautApplicationBuilder then(
        Micronaut.MicronautApplicationBuilder self,
        @DelegatesTo(value = ApplicationContext.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "io.micronaut.context.ApplicationContext")
            Closure<ApplicationContext> configuration
    ) {
        return self.doWithContext(ConsumerWithDelegate.create(configuration));
    }

}
