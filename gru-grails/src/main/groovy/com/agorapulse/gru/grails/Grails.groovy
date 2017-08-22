package com.agorapulse.gru.grails

import com.agorapulse.gru.AbstractClient
import com.agorapulse.gru.Client
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.grails.minions.UrlMappingsMinion
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
        super('', unitTest)
    }

    @Override
    Client.Request getRequest() {
        return request = request ?: new GruGrailsRequest(unitTest.request, unitTest)
    }

    @Override
    Client.Response getResponse() {
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
        String actionName = squad.ask(UrlMappingsMinion) { getActionName(unitTest) }

        context.withResult(controllerClass.invoke(unitTest.controller, actionName))
    }

    U getUnitTest() {
        this.@unitTest as U
    }

    @Override
    List<Minion> getInitialSquad() {
        return [new UrlMappingsMinion()]
    }
}
