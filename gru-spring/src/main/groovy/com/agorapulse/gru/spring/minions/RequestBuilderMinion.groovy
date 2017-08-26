package com.agorapulse.gru.spring.minions

import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.minions.AbstractMinion
import com.agorapulse.gru.spring.Spring
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

/**
 * Request builder minion allows to add more request customisation based on standard MockHttpServletRequestBuilder.
 */
class RequestBuilderMinion extends AbstractMinion<Spring> {

    final int index = PARAMETERS_MINION_INDEX + 1000

    private final List<Closure<MockHttpServletRequestBuilder>> steps = []

    RequestBuilderMinion() {
        super(Spring)
    }

    @Override
    protected GruContext doBeforeRun(Spring client, Squad squad, GruContext context) {
        for (Closure step : steps) {
            client.request.addBuildStep step
        }
        return context
    }

    void addBuildStep(@DelegatesTo(MockHttpServletRequestBuilder) Closure<MockHttpServletRequestBuilder> step) {
        steps << step
    }
}
