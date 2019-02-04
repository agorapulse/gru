package com.agorapulse.gru.spring.minions

import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.minions.AbstractMinion
import com.agorapulse.gru.spring.Spring
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import space.jasan.support.groovy.closure.ConsumerWithDelegate

import java.util.function.Consumer

/**
 * Request builder minion allows to add more request customisation based on standard MockHttpServletRequestBuilder.
 */
class RequestBuilderMinion extends AbstractMinion<Spring> {

    final int index = PARAMETERS_MINION_INDEX + 1000

    private final List<Consumer<MockHttpServletRequestBuilder>> steps = []

    RequestBuilderMinion() {
        super(Spring)
    }

    void addBuildStep(@DelegatesTo(MockHttpServletRequestBuilder) Closure<MockHttpServletRequestBuilder> step) {
        addBuildStep(ConsumerWithDelegate.create(step))
    }

    void addBuildStep(Consumer<MockHttpServletRequestBuilder> step) {
        steps << step
    }

    @Override
    protected GruContext doBeforeRun(Spring client, Squad squad, GruContext context) {
        for (Consumer<MockHttpServletRequestBuilder> step : steps) {
            client.request.addBuildStep step
        }
        return context
    }

}
