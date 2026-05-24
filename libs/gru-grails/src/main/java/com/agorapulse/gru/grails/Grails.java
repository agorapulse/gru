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
package com.agorapulse.gru.grails;

import com.agorapulse.gru.AbstractClient;
import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;
import com.agorapulse.gru.grails.minions.ControllerInitializationMinion;
import com.agorapulse.gru.grails.minions.GrailsHtmlMinion;
import com.agorapulse.gru.grails.minions.UrlMappingsMinion;
import com.agorapulse.gru.grails.minions.jsonview.JsonViewRendererMinion;
import com.agorapulse.gru.grails.minions.jsonview.JsonViewSupport;
import com.agorapulse.gru.minions.Minion;
import grails.core.GrailsControllerClass;
import grails.testing.web.controllers.ControllerUnitTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Grails Gru client leverages Grails Testing Support to allow REST-like testing within Grails unit test.
 * @param <U> type of the unit test being tested
 */
public class Grails<U extends ControllerUnitTest<?>> extends AbstractClient {

    public static <U extends ControllerUnitTest<?>> Grails<U> create(U unitTest) {
        return new Grails<>(unitTest);
    }

    private GruGrailsRequest request;
    private GruGrailsResponse response;

    private Grails(U unitTest) {
        super(unitTest);
    }

    @Override
    public GruGrailsRequest getRequest() {
        if (request == null) {
            request = new GruGrailsRequest(getUnitTest().getRequest(), getUnitTest());
        }
        return request;
    }

    @Override
    public GruGrailsResponse getResponse() {
        if (response == null) {
            response = new GruGrailsResponse(getUnitTest().getResponse());
        }
        return response;
    }

    @Override
    public void reset() {
        this.request = null;
        this.response = null;
    }

    @Override
    public GruContext run(Squad squad, GruContext context) {
        U test = getUnitTest();
        GrailsControllerClass controllerClass = squad.ask(UrlMappingsMinion.class, m -> m.getControllerClass(test));

        if (controllerClass == null) {
            String controllerName = squad.ask(UrlMappingsMinion.class, m -> m.getControllerName(test));
            return context.withError(new AssertionError("The URL is not mapped or the controller '" + controllerName + "' is not mocked!"));
        }

        String actionName = squad.ask(UrlMappingsMinion.class, m -> m.getActionName(test));

        if (!controllerClass.getActions().contains(actionName)) {
            String controllerName = squad.ask(UrlMappingsMinion.class, m -> m.getControllerName(test));
            return context.withError(new AssertionError("Action '" + actionName + "' does not exist in controller '" + controllerName + "'"));
        }

        try {
            Object result = controllerClass.invoke(test.getController(), actionName);
            return context.withResult(result);
        } catch (Throwable t) {
            return context.withError(t);
        }
    }

    @SuppressWarnings("unchecked")
    public U getUnitTest() {
        return (U) super.getUnitTest();
    }

    @Override
    public List<Minion> getInitialSquad() {
        List<Minion> minions = new ArrayList<>();
        minions.add(new ControllerInitializationMinion());
        minions.add(new UrlMappingsMinion());
        minions.add(new GrailsHtmlMinion());

        if (JsonViewSupport.isEnabled()) {
            minions.add(new JsonViewRendererMinion());
        }

        return minions;
    }
}
