package com.agorapulse.gru.spring.heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.spring.Spring
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

@WebMvcTest                                                                             // <1>
class BasicSpec extends Specification {

    @Autowired MockMvc mvc                                                              // <2>

    @Rule Gru<Spring> gru = Gru.equip(Spring.steal(this))                               // <3>

    void 'look at the moon'() {
        expect:
            gru.test {
                get '/moons/earth/moon'                                                 // <4>
            }
    }
}
