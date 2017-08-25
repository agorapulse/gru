package com.agorapulse.gru.spring

import com.agorapulse.gru.AbstractClient
import com.agorapulse.gru.Client
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request

/**
 * Gru implementation based on MockMvc.
 */
class Spring extends AbstractClient {

    static Spring steal(Object unitTest) {
        return new Spring(unitTest)
    }

    private GruSpringRequest request
    private GruSpringResponse response

    private Spring(Object unitTest) {
        super(unitTest)
        reset()
    }

    @Override
    Client.Request getRequest() {
        return request
    }

    @Override
    Client.Response getResponse() {
        return response
    }

    @Override
    void reset() {
        request = new GruSpringRequest()
        response = null
    }

    @Override
    @SuppressWarnings('Instanceof')
    GruContext run(Squad squad, GruContext context) {
        MockMvc mockMvc

        if (unitTest.hasProperty('mockMvc')) {
            mockMvc = unitTest.mockMvc as MockMvc
        } else if (unitTest.hasProperty('mvc')) {
            mockMvc = unitTest.mvc as MockMvc
        } else {
            mockMvc = unitTest.properties.find { it instanceof MockMvc } as MockMvc
        }

        if (!mockMvc) {
            throw new IllegalStateException('MockMvc is missing in the unit test or it is null. ' +
                'Please provide \'@Autowired MockMvc mockMvc\' field in your specification')
        }

        MvcResult result = mockMvc.perform(request(request.method, requestURI)).andReturn()
        response = new GruSpringResponse(result.response)
        return context.withResult(result)
    }

    private URI getRequestURI() {
        new URI("${request.baseUri ?: ''}${request.uri ?: ''}".replaceAll('/+', '/'))
    }

    @Override
    Object getUnitTest() {
        return this.@unitTest
    }
}
