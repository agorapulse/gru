package com.agorapulse.gru.spring.heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.spring.Spring
import com.agorapulse.gru.spring.minions.ResultMatcherMinion
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

@WebMvcTest
class MoonController4Spec extends Specification {

    @Rule Gru<Spring> gru = Gru.equip(Spring.steal(this))

    void 'there is no mockmvc'() {
        when:
            gru.test {
                get '/moons/earth/moon'
            }.verify()
        then:
            AssertionError error = thrown(AssertionError)
            error.cause instanceof IllegalStateException
            error.cause.message
            error.cause.message.startsWith('MockMvc is missing in the unit test or it is null')
    }

}
