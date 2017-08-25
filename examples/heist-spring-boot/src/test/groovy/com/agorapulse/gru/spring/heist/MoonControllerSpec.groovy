package com.agorapulse.gru.spring.heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.spring.Spring
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification


@WebMvcTest
class MoonControllerSpec extends Specification {

    @Autowired MockMvc mvc

    @Rule Gru<Spring> gru = Gru.equip(Spring.steal(this))

    void 'json is rendered'() {
        assert mvc
        expect:
            gru.test {
                get '/moons/earth/moon'
                expect {
                    headers 'Content-Type': 'application/json;charset=UTF-8'
                }
            }
    }

}
