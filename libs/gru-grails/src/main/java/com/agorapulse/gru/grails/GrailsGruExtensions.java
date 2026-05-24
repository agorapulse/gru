/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2026 Agorapulse.
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
package com.agorapulse.gru.grails;

import com.agorapulse.gru.RequestDefinitionBuilder;
import com.agorapulse.gru.ResponseDefinitionBuilder;
import com.agorapulse.gru.TestDefinitionBuilder;
import com.agorapulse.gru.grails.minions.ForwardMinion;
import com.agorapulse.gru.grails.minions.InterceptorsMinion;
import com.agorapulse.gru.grails.minions.ModelMinion;
import com.agorapulse.gru.grails.minions.UrlMappingsMinion;
import groovy.lang.Closure;
import org.codehaus.groovy.runtime.MethodClosure;

/**
 * Adds convenient methods for Grails to the test definition DSL.
 */
public final class GrailsGruExtensions {

    private GrailsGruExtensions() {
    }

    public static String getUrlMappings(TestDefinitionBuilder builder) {
        return "UrlMappings";
    }

    public static TestDefinitionBuilder include(TestDefinitionBuilder self, Class<?> type) {
        return include(self, type, false);
    }

    /**
     * Include artifact by class reference. URL mappings and interceptors are supported.
     *
     * @param self the test definition builder
     * @param type class of the artefact
     * @param autowire whether the artefact should be autowired
     * @return self
     */
    public static TestDefinitionBuilder include(TestDefinitionBuilder self, Class<?> type, boolean autowire) {
        String typeName = type.getName();
        if (typeName.endsWith("UrlMappings")) {
            if (autowire) {
                throw new IllegalArgumentException("UrlMappings cannot be wired automatically");
            }
            self.command(UrlMappingsMinion.class, minion -> minion.getUrlMappings().add(type));
        } else if (typeName.endsWith("Interceptor")) {
            self.command(InterceptorsMinion.class, minion -> {
                minion.getInterceptors().add(type);
                minion.getAutowired().add(type);
            });
        } else {
            throw new IllegalArgumentException("Unknown type of artefact: " + typeName);
        }
        return self;
    }

    /**
     * Verifies that the URL given for this test is mapped to the supplied controller action.
     *
     * @param method controller method to check; pass via {@code controller.&method} to get a type-safe reference
     * @return self
     */
    public static RequestDefinitionBuilder executes(RequestDefinitionBuilder self, Closure<?> method) {
        if (!(method instanceof MethodClosure)) {
            throw new IllegalArgumentException("Closure must be a method closure. Use controller.&methodname to get a type-safe reference.");
        }
        MethodClosure methodClosure = (MethodClosure) method;
        self.command(UrlMappingsMinion.class, minion -> minion.setAction(methodClosure));
        return self;
    }

    /**
     * Sets the expected model returned from the controller action.
     */
    public static ResponseDefinitionBuilder model(ResponseDefinitionBuilder self, Object aModel) {
        self.command(ModelMinion.class, minion -> minion.setModel(aModel));
        return self;
    }

    /**
     * Sets the expected forward URI.
     */
    public static ResponseDefinitionBuilder forward(ResponseDefinitionBuilder self, String url) {
        self.command(ForwardMinion.class, minion -> minion.setForwardedUri(url));
        return self;
    }
}
