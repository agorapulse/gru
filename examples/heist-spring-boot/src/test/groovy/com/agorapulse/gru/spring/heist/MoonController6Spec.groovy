package com.agorapulse.gru.spring.heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.spring.Spring
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

@WebMvcTest
class MoonController6Spec extends Specification {

    private static final String WHATEVER_JSON = '{ "foo" : "bar" }'

    @Autowired MockMvc whatever

    @Rule Gru<Spring> gru = Gru.equip(Spring.steal(this))

    void 'using content'() {
        expect:
            gru.test {
                get '/moons/json-echo', {
                    content inline(WHATEVER_JSON), 'application/json'
                }
                expect {
                    json inline(WHATEVER_JSON)
                }
            }
    }

}
