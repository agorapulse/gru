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
package com.agorapulse.gru.spring.minions;

import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;
import com.agorapulse.gru.minions.AbstractMinion;
import com.agorapulse.gru.spring.Spring;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Result matcher minion allows to append standard MockMvc result matchers inside the expect block.
 */
public class ResultMatcherMinion extends AbstractMinion<Spring> {

    private final List<ResultMatcher> matchers = new ArrayList<>();

    public ResultMatcherMinion() {
        super(Spring.class);
    }

    public ResultMatcherMinion addMatcher(ResultMatcher matcher) {
        matchers.add(matcher);
        return this;
    }

    @Override
    @SuppressWarnings("Instanceof")
    protected void doVerify(Spring client, Squad squad, GruContext context) throws Throwable {
        if (matchers.isEmpty()) {
            return;
        }

        if (!(context.getResult() instanceof MvcResult)) {
            throw new AssertionError("Cannot perform assertion, result is not MvcResult. Was: " + context.getResult());
        }

        MvcResult result = DefaultGroovyMethods.asType(context.getResult(), MvcResult.class);
        for (ResultMatcher matcher : matchers) {
            matcher.match(result);
        }
    }

    public final int getIndex() {
        return MODEL_MINION_INDEX + 1000;
    }

}
