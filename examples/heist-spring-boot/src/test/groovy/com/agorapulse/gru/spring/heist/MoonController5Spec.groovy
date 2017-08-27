package com.agorapulse.gru.spring.heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.spring.Spring
import com.agorapulse.gru.spring.minions.ResultMatcherMinion
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest
class MoonController5Spec extends Specification {

    @Autowired MockMvc mvc

    @Rule Gru<Spring> gru = Gru.equip(Spring.steal(this))
                               .engage(new ResultMatcherMinion())
                               .engage(new NastyMinion())

    void 'nasty minion'() {
        when:
            gru.test {
                get '/moons/earth/moon'
                expect {
                    that status().isOk()
                }
            }.verify()
        then:
            AssertionError error = thrown(AssertionError)
            error.message
            error.message.startsWith('Cannot perform assertion')
    }

}
