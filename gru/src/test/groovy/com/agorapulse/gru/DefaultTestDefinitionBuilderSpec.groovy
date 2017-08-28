package com.agorapulse.gru

import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Modifier

/**
 * Test for DefaultTestDefintionBuilder.
 */
class DefaultTestDefinitionBuilderSpec extends Specification {

    private static final String URI = '/about'
    private static final String PARAMETER_KEY = 'foo'
    private static final String PARAMETER_VALUE = 'bar'
    private static final Map<String, String> PARAMS = [(PARAMETER_KEY): (PARAMETER_VALUE)]

    @Unroll
    @SuppressWarnings('UnnecessaryGetter')
    void "calls client with proper method #method"() {
        given:
            Object unitTest = this

            Client.Request request = Mock(Client.Request)

            Client client = Mock(Client) {
                getRequest() >> request
                getUnitTest() >> unitTest
            }

            Squad squad = new Squad()
            DefaultTestDefinitionBuilder builder = new DefaultTestDefinitionBuilder(client, squad)

            String methodName = method.toLowerCase()
        when:
            builder."$methodName"(URI)
            builder."$methodName"(URI) {
                params PARAMS
            }
            squad.beforeRun(client, GruContext.EMPTY)
        then:
            2 * request.setUri(URI)
            2 * request.setMethod(method)
            1 * request.addParameter(PARAMETER_KEY, PARAMETER_VALUE)
        where:
            method << HttpVerbsShortcuts.fields.findAll {
                Modifier.isStatic(it.modifiers) && Modifier.isFinal(it.modifiers) && it.type == String
            } *.name
    }

}
