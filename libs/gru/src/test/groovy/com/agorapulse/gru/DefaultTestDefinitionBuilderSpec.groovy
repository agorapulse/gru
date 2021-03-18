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
package com.agorapulse.gru

import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Modifier

/**
 * Test for DefaultTestDefintionBuilder.
 */
class DefaultTestDefinitionBuilderSpec extends Specification {

    private static final String URI = '/about'
    private static final String PARAMETER_KEY = 'foo'
    private static final String PARAMETER_VALUE = 'bar'
    private static final Map<String, String> PARAMS = [(PARAMETER_KEY): (PARAMETER_VALUE)]

    @Unroll
    @SuppressWarnings('UnnecessaryGetter')
    void "calls client with proper method #method"() {
        given:
            Object unitTest = this

            Client.Request request = Mock(Client.Request)

            Client client = Mock(Client) {
                getRequest() >> request
                getUnitTest() >> unitTest
            }

            Squad squad = new Squad()
            DefaultTestDefinitionBuilder builder = new DefaultTestDefinitionBuilder(client, squad)

            String methodName = method.toLowerCase()
        when:
            builder."$methodName"(URI)
            builder."$methodName"(URI) {
                params PARAMS
            }
            squad.beforeRun(client, GruContext.EMPTY)
        then:
            2 * request.setUri(URI)
            2 * request.setMethod(method)
            1 * request.addParameter(PARAMETER_KEY, PARAMETER_VALUE)
        where:
            method << HttpVerbsShortcuts.fields.findAll {
                Modifier.isStatic(it.modifiers) && Modifier.isFinal(it.modifiers) && it.type == String
            } *.name
    }

}
