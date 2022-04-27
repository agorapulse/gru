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
package com.agorapulse.gru.spring.groovy;

import com.agorapulse.gru.RequestDefinitionBuilder;
import com.agorapulse.gru.ResponseDefinitionBuilder;
import com.agorapulse.gru.spring.GruSpringRequest;
import com.agorapulse.gru.spring.minions.RequestBuilderMinion;
import com.agorapulse.gru.spring.minions.ResultMatcherMinion;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import space.jasan.support.groovy.closure.ConsumerWithDelegate;

import java.util.function.Consumer;

/**
 * Add convenient methods for Grails to test definition DSL.
 */
public class GruSpringExtensions {

    public static RequestDefinitionBuilder and(
        RequestDefinitionBuilder self,
        @DelegatesTo(value = MockHttpServletRequestBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder")
            Closure<MockHttpServletRequestBuilder> step
    ) {
        return and(self, ConsumerWithDelegate.create(step));
    }

    public static RequestDefinitionBuilder and(RequestDefinitionBuilder self, Consumer<MockHttpServletRequestBuilder> step) {
        return request(self, step);
    }

    public static RequestDefinitionBuilder request(
        RequestDefinitionBuilder self,
        @DelegatesTo(value = MockHttpServletRequestBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder")
            Closure<MockHttpServletRequestBuilder> step
    ) {
        return request(self, ConsumerWithDelegate.create(step));
    }

    public static RequestDefinitionBuilder request(RequestDefinitionBuilder self, Consumer<MockHttpServletRequestBuilder> step) {
        return self.command(RequestBuilderMinion.class, request -> request.addBuildStep(step));
    }

    public static ResponseDefinitionBuilder that(ResponseDefinitionBuilder self, ResultMatcher matcher) {
        return self.command(ResultMatcherMinion.class, request-> request.addMatcher(matcher));
    }

    public static ResponseDefinitionBuilder and(ResponseDefinitionBuilder self, ResultMatcher matcher) {
        return that(self, matcher);
    }

    public static void addBuildStep(
        RequestBuilderMinion self,
        @DelegatesTo(MockHttpServletRequestBuilder.class)
        @ClosureParams(value = SimpleType.class, options = "org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder")
        Closure<MockHttpServletRequestBuilder> step
    ) {
        self.addBuildStep(ConsumerWithDelegate.create(step));
    }


    public static void addBuildStep(
        GruSpringRequest self,
        @DelegatesTo(value = MockHttpServletRequestBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder")
        Closure<MockHttpServletRequestBuilder> step
    ) {
        self.addBuildStep(ConsumerWithDelegate.create(step));
    }

}
