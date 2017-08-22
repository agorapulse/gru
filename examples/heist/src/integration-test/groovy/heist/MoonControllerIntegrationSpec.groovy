package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.http.Http
import grails.testing.mixin.integration.Integration
import org.junit.Rule
import org.springframework.beans.factory.annotation.Value
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@Integration
class MoonControllerIntegrationSpec extends Specification {

    @Value('${local.server.port}')
    Integer serverPort

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
                get('/moon')
                expect {
                    json 'indexResponse.json'
                }
            }
    }

    void 'teapot'() {
        expect:
            gru.test {
                get('/moon/teapot')
                expect {
                    status I_AM_A_TEAPOT
                }
            }
    }

    void 'redirect to teapot'() {
        expect:
            gru.test {
                get('/moon/redirectToTeapot')
                expect {
                    redirect '/moon/teapot'
                }
            }
    }

    void 'echo'() {
        expect:
            gru.test {
                post '/moon/echo', {
                    json 'echoRequest.json'
                }
                expect {
                    json 'echoResponse.json'
                }
            }
    }
}
