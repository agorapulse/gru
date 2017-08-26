package com.agorapulse.gru.spring.minions

import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.minions.AbstractMinion
import com.agorapulse.gru.spring.Spring
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultMatcher

/**
 * Result matcher minion allows to append standard MockMvc result matchers inside the expect block.
 */
class ResultMatcherMinion extends AbstractMinion<Spring> {

    final int index = MODEL_MINION_INDEX + 1000

    private final List<ResultMatcher> matchers = []

    ResultMatcherMinion() {
        super(Spring)
    }

    @Override
    @SuppressWarnings('Instanceof')
    protected void doVerify(Spring client, Squad squad, GruContext context) throws Throwable {
        if (!matchers) {
            return
        }
        if (!(context.result instanceof MvcResult)) {
            throw new AssertionError("Cannot perform assertion, result is not MvcResult. Was: $context.result")
        }
        MvcResult result = context.result as MvcResult
        for (ResultMatcher matcher in matchers) {
            matcher.match(result)
        }
    }

    void addMatcher(ResultMatcher matcher) {
        matchers << matcher
    }
}
