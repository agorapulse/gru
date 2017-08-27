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
class MoonController3Spec extends Specification {

    @Autowired MockMvc whatever

    @Rule Gru<Spring> gru = Gru.equip(Spring.steal(this))

    void 'use Gru for testing'() {
        expect:
            gru.test {
                get '/moons/json-echo', {
                    json 'whatever.json'
                }
                expect {
                    json 'whatever.json'
                }
            }
    }

}
