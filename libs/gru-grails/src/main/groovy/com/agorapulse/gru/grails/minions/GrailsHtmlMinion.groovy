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
package com.agorapulse.gru.grails.minions

import com.agorapulse.gru.Client
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.grails.Grails
import com.agorapulse.gru.minions.HtmlMinion
import groovy.transform.CompileStatic
import groovy.util.logging.Log

/**
 * Minion responsible for HTML responses which is able to generate HTML from view.
 */
@Log @CompileStatic
class GrailsHtmlMinion extends HtmlMinion {

    @Override
    @SuppressWarnings('Instanceof')
    protected String readResponseText(Client client, Squad squad, GruContext context) {
        Grails grails = client as Grails
        String actualResponseText = client.response.text

        if (!actualResponseText && context.result instanceof Map) {
            String actionName = squad.ask(UrlMappingsMinion) { UrlMappingsMinion url -> url.getActionName(grails.unitTest) }
            return grails.unitTest.render(view: actionName, model: context.result as Map)
        }
        return actualResponseText
    }

}
