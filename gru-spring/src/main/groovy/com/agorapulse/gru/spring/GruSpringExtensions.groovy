package com.agorapulse.gru.spring

import com.agorapulse.gru.RequestDefinitionBuilder
import com.agorapulse.gru.ResponseDefinitionBuilder
import com.agorapulse.gru.spring.minions.RequestBuilderMinion
import com.agorapulse.gru.spring.minions.ResultMatcherMinion
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

/**
 * Add convenient methods for Grails to test definition DSL.
 */
class GruSpringExtensions {

    static RequestDefinitionBuilder and(RequestDefinitionBuilder self,
            @DelegatesTo(MockHttpServletRequestBuilder) Closure<MockHttpServletRequestBuilder> step) {

        request(self, step)
    }

    static RequestDefinitionBuilder request(RequestDefinitionBuilder self,
            @DelegatesTo(MockHttpServletRequestBuilder) Closure<MockHttpServletRequestBuilder> step) {

        self.command(RequestBuilderMinion) {
            addBuildStep step
        }
    }

    static ResponseDefinitionBuilder that(ResponseDefinitionBuilder self, ResultMatcher matcher) {
        self.command(ResultMatcherMinion) {
            addMatcher matcher
        }
    }

    static ResponseDefinitionBuilder and(ResponseDefinitionBuilder self, ResultMatcher matcher) {
        that self, matcher
    }

}
