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
package com.agorapulse.gru.spring.minions

import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.minions.AbstractMinion
import com.agorapulse.gru.spring.Spring
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import space.jasan.support.groovy.closure.ConsumerWithDelegate

import java.util.function.Consumer

/**
 * Request builder minion allows to add more request customisation based on standard MockHttpServletRequestBuilder.
 */
@CompileStatic
class RequestBuilderMinion extends AbstractMinion<Spring> {

    final int index = PARAMETERS_MINION_INDEX + 1000

    private final List<Consumer<MockHttpServletRequestBuilder>> steps = []

    RequestBuilderMinion() {
        super(Spring)
    }

    void addBuildStep(
        @DelegatesTo(MockHttpServletRequestBuilder)
        @ClosureParams(value = SimpleType, options = 'org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder')
            Closure<MockHttpServletRequestBuilder> step
    ) {
        addBuildStep(ConsumerWithDelegate.create(step))
    }

    void addBuildStep(Consumer<MockHttpServletRequestBuilder> step) {
        steps << step
    }

    @Override
    @CompileDynamic
    protected GruContext doBeforeRun(Spring client, Squad squad, GruContext context) {
        for (Consumer<MockHttpServletRequestBuilder> step : steps) {
            client.request.addBuildStep step
        }
        return context
    }

}
