package com.agorapulse.gru.spring.heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.http.Http
import org.junit.Rule
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MoonControllerIntegrationTest extends Specification {

    @Value('${local.server.port}')
    private int serverPort

    @Rule Gru<Http> gru = Gru.equip(Http.steal(this))

    void setup() {
        final String serverUrl = "http://localhost:${serverPort}"
        gru.prepare {
            baseUri serverUrl
        }
    }

    void 'render json'() {
        expect:
            gru.test {
                get('/moons/earth/moon')
                expect {
                    json 'renderJsonResponse.json'
                }
            }
    }


}
