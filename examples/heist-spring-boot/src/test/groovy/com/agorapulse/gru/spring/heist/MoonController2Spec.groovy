package com.agorapulse.gru.spring.heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.spring.Spring
import com.agorapulse.gru.spring.minions.ResultMatcherMinion
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content

@WebMvcTest
class MoonController2Spec extends Specification {

    @Autowired MockMvc mockMvc

    @Rule Gru<Spring> gru = Gru.equip(Spring.steal(this)).engage(new ResultMatcherMinion())

    void 'use Gru for testing'() {
        expect:
            gru.test {
                get '/moons/headers-echo', {
                    headers 'X-Simon-Says': 'Use Gru for testing!'
                }
                expect {
                    json build {
                        "X-Simon-Says" "Use Gru for testing!"
                    }
                }
            }
    }

}
