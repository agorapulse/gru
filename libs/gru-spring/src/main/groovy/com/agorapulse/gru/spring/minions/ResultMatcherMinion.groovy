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
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultMatcher

/**
 * Result matcher minion allows to append standard MockMvc result matchers inside the expect block.
 */
@CompileStatic
class ResultMatcherMinion extends AbstractMinion<Spring> {

    final int index = MODEL_MINION_INDEX + 1000

    private final List<ResultMatcher> matchers = []

    ResultMatcherMinion() {
        super(Spring)
    }

    void addMatcher(ResultMatcher matcher) {
        matchers << matcher
    }

    @Override
    @CompileDynamic
    @SuppressWarnings('Instanceof')
    protected void doVerify(Spring client, Squad squad, GruContext context) throws Throwable {
        if (!matchers) {
            return
        }
        if (!(context.result instanceof MvcResult)) {
            throw new AssertionError("Cannot perform assertion, result is not MvcResult. Was: $context.result")
        }
        MvcResult result = context.result as MvcResult
        for (ResultMatcher matcher in matchers) {
            matcher.match(result)
        }
    }

}
