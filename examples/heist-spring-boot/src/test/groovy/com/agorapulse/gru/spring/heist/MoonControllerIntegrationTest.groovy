package com.agorapulse.gru.spring.heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.http.Http
import org.junit.Rule
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)             // <1>
class MoonControllerIntegrationTest extends Specification {

    @Value('${local.server.port}') private int serverPort                               // <2>

    @Rule Gru<Http> gru = Gru.equip(Http.steal(this))                                   // <3>

    void setup() {
        gru.prepare {
            baseUri "http://localhost:${serverPort}"                                    // <4>
        }
    }

    void 'render json'() {                                                              // <5>
        expect:
            gru.test {
                get('/moons/earth/moon')
                expect {
                    json 'renderJsonResponse.json'
                }
            }
    }

}
