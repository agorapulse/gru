package com.agorapulse.gru.grails

import com.agorapulse.gru.AbstractClient
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.grails.minions.ControllerInitializationMinion
import com.agorapulse.gru.grails.minions.GrailsHtmlMinion
import com.agorapulse.gru.grails.minions.jsonview.JsonViewRendererMinion
import com.agorapulse.gru.grails.minions.UrlMappingsMinion
import com.agorapulse.gru.grails.minions.jsonview.JsonViewSupport
import com.agorapulse.gru.minions.Minion
import grails.core.GrailsControllerClass
import grails.testing.web.controllers.ControllerUnitTest

/**
 * Grails Gru client leverages Grails Testing Support library to allow REST-like testing within Grails unit test.
 * @param <U> type of the unit test being tested
 */
class Grails<U extends ControllerUnitTest<?>> extends AbstractClient {

    static <U extends ControllerUnitTest<?>> Grails<U> steal(U unitTest) {
        return new Grails<U>(unitTest)
    }

    private GruGrailsRequest request
    private GruGrailsResponse response

    private Grails(U unitTest) {
        super(unitTest)
    }

    @Override
    GruGrailsRequest getRequest() {
        return request = request ?: new GruGrailsRequest(unitTest.request, unitTest)
    }

    @Override
    GruGrailsResponse getResponse() {
        return response = response ?: new GruGrailsResponse(unitTest.response)
    }

    @Override
    void reset() {
        this.request = null
        this.response = null
    }

    @Override
    GruContext run(Squad squad, GruContext context) {
        GrailsControllerClass controllerClass = squad.ask(UrlMappingsMinion) { getControllerClass(unitTest) }

        if (!controllerClass) {
            String controllerName = squad.ask(UrlMappingsMinion) { getControllerName(unitTest) }
            return context.withError(new AssertionError("The URL is not mapped or the controller '$controllerName' is not mocked!"))
        }

        String actionName = squad.ask(UrlMappingsMinion) { getActionName(unitTest) }

        if (!controllerClass.actions.contains(actionName)) {
            String controllerName = squad.ask(UrlMappingsMinion) { getControllerName(unitTest) }
            return context.withError(new AssertionError("Action '$actionName' does not exist in controller '$controllerName'"))
        }

        Object result = controllerClass.invoke(unitTest.controller, actionName)
        context.withResult(result)
    }

    U getUnitTest() {
        super.unitTest as U
    }

    @Override
    List<Minion> getInitialSquad() {
        List<Minion> minions = [new ControllerInitializationMinion(), new UrlMappingsMinion(), new GrailsHtmlMinion()]

        if (JsonViewSupport.enabled) {
            minions << new JsonViewRendererMinion()
        }

        return minions
    }
}
