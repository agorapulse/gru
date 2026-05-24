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
package com.agorapulse.gru.grails.minions;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;
import com.agorapulse.gru.grails.Grails;
import com.agorapulse.gru.minions.HtmlMinion;

import java.util.HashMap;
import java.util.Map;

/**
 * Minion responsible for HTML responses; renders the Grails view when the
 * controller returned a model instead of writing to the response directly.
 */
public class GrailsHtmlMinion extends HtmlMinion {

    @Override
    protected String readResponseText(Client client, Squad squad, GruContext context) {
        Grails grails = (Grails) client;
        String actualResponseText = client.getResponse().getText();

        if ((actualResponseText == null || actualResponseText.isEmpty()) && context.getResult() instanceof Map) {
            String actionName = squad.ask(UrlMappingsMinion.class, url -> url.getActionName(grails.getUnitTest()));
            Map<String, Object> renderArgs = new HashMap<>();
            renderArgs.put("view", actionName);
            renderArgs.put("model", new HashMap<>((Map<?, ?>) context.getResult()));
            return grails.getUnitTest().render(renderArgs);
        }
        return actualResponseText;
    }
}
