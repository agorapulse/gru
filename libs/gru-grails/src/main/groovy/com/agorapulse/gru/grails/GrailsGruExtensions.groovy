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
package com.agorapulse.gru.grails

import com.agorapulse.gru.RequestDefinitionBuilder
import com.agorapulse.gru.ResponseDefinitionBuilder
import com.agorapulse.gru.TestDefinitionBuilder
import com.agorapulse.gru.grails.minions.ForwardMinion
import com.agorapulse.gru.grails.minions.InterceptorsMinion
import com.agorapulse.gru.grails.minions.ModelMinion
import com.agorapulse.gru.grails.minions.UrlMappingsMinion
import grails.testing.web.controllers.ControllerUnitTest
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure

@SuppressWarnings(['ClassStartsWithBlankLine', 'ClassEndsWithBlankLine'])

// tag::header[]
/**
 * Add convenient methods for Grails to test definition DSL.
 */
@CompileStatic
class GrailsGruExtensions {
// end::header[]

    /**
     * Include artifact by class reference.
     *
     * URL mappings and interceptors are supported at the moment.
     *
     * @param type class of the artifact
     * @param autowire whether the artifact should be autowired, defaults to <code>false</code>
     * @return self
     */
    static <U extends ControllerUnitTest<?>> TestDefinitionBuilder include(TestDefinitionBuilder self, Class type, boolean autowire = false) {
        if (type.name.endsWith('UrlMappings')) {
            if (autowire) {
                throw new IllegalArgumentException('UrlMappings cannot be wired automatically')
            }
            self.command(UrlMappingsMinion) { urlMappings.add(type) }
        } else if (type.name.endsWith('Interceptor')) {
            self.command(InterceptorsMinion) {
                interceptors.add(type)
                autowired.add(type)
            }
        } else {
            throw new IllegalArgumentException('Unknown type of artefact: ' + type.name)
        }
        return self
    }

    /**
     * Verifies that URL given for this test is mapped to given controller action.
     * @param method controller method which should be checked for mapping, use controller.&method to obtain the reference
     * @return self
     */
    @SuppressWarnings('Instanceof')
    static <U extends ControllerUnitTest> RequestDefinitionBuilder executes(RequestDefinitionBuilder self, Closure method) {
        if (!(method instanceof MethodClosure)) {
            throw new IllegalArgumentException('Closure must be method closure. Use controller.&methodname to get type safe method closure!')
        }

        return self.command(UrlMappingsMinion) {
            action = method as MethodClosure
        }
    }

    // tag::model[]
    /**
     * Sets the expected model returned from the controller action.
     *
     * @param aModel expected model
     * @return self
     */
    static ResponseDefinitionBuilder model(ResponseDefinitionBuilder self, Object aModel) { // <1>
        return self.command(ModelMinion) {                                                  // <2>
            model = aModel
        }
    }
    // end::model[]

    /**
     * Sets the expected forward URI.
     * @param uri expected URI
     * @return self
     */
    static ResponseDefinitionBuilder forward(ResponseDefinitionBuilder self, String url) {
        return self.command(ForwardMinion) {
            forwardedUri = url
        }
    }

// tag::footer[]
}
// end::footer[]
