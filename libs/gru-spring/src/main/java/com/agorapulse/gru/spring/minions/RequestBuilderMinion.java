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
package com.agorapulse.gru.spring.minions;

import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;
import com.agorapulse.gru.minions.AbstractMinion;
import com.agorapulse.gru.spring.Spring;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Request builder minion allows to add more request customisation based on standard MockHttpServletRequestBuilder.
 */
public class RequestBuilderMinion extends AbstractMinion<Spring> {
    private final List<Consumer<MockHttpServletRequestBuilder>> steps = new ArrayList<>();

    public RequestBuilderMinion() {
        super(Spring.class);
    }

    public void addBuildStep(Consumer<MockHttpServletRequestBuilder> step) {
        steps.add(step);
    }

    public final int getIndex() {
        return PARAMETERS_MINION_INDEX + 1000;
    }

    @Override
    protected GruContext doBeforeRun(Spring client, Squad squad, GruContext context) {
        for (Consumer<MockHttpServletRequestBuilder> step : steps) {
            client.getRequest().addBuildStep(step);
        }

        return context;
    }


}
