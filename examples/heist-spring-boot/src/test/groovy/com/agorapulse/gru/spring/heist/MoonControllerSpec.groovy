package com.agorapulse.gru.spring.heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.spring.Spring
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest
class MoonControllerSpec extends Specification {

    @Autowired MockMvc mvc

    @Rule Gru<Spring> gru = Gru.equip(Spring.steal(this))

    void 'json is rendered'() {
        expect:
            gru.test {
                get '/moons/earth/moon', {
                    request {
                        accept(MediaType.APPLICATION_JSON_UTF8)
                    }
                    and {
                        locale(Locale.CANADA)
                    }
                }
                expect {
                    headers 'Content-Type': 'application/json;charset=UTF-8'
                    json 'moonResponse.json'
                    that content().encoding('UTF-8')
                    and content().contentType(MediaType.APPLICATION_JSON_UTF8)
                }
            }
    }

    void 'the only true moon'() {
        expect:
            gru.test {
                get '/moons/the-only-true-moon'
                expect {
                    redirect '/moons/earth/moon'
                }
            }
    }

    void 'params echo'() {
        expect:
            gru.test {
                get('/moons/params-echo') {
                    params foo: 'bar', zoo: 'fun'
                }
                expect {
                    json 'paramsEchoResponse.json'
                }
            }
    }

}
