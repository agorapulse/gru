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
package com.agorapulse.gru.minions;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Minion responsible for URL parameters.
 */
public class ParametersMinion extends AbstractMinion<Client> {

    private final Map<String, Object> parameters = new LinkedHashMap<>();

    public ParametersMinion() {
        super(Client.class);
    }

    @Override
    public GruContext doBeforeRun(final Client client, Squad squad, GruContext context) {
        if (!parameters.isEmpty()) {
            parameters.forEach((key, value) -> client.getRequest().addParameter(key, value));
        }

        return context;
    }

    public void addParameters(Map<String, Object> params) {
        parameters.putAll(params);
    }

    public void addParameter(String key, Object value) {
        parameters.put(key, value);
    }

    public final int getIndex() {
        return PARAMETERS_MINION_INDEX;
    }


}
