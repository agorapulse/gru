package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.grails.Grails
import grails.testing.web.controllers.ControllerUnitTest
import org.junit.Rule
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
class MoonControllerSpec extends Specification implements ControllerUnitTest<MoonController> {

    @Rule Gru<Grails<MoonControllerSpec>> gru = Gru.equip(Grails.steal(this)).prepare {
        include UrlMappings
    }

    void 'render json'() {
        expect:
            gru.test {
                get('/moon') {
                    executes controller.&index
                }
                expect {
                    json 'indexResponse.json'
                }
            }
    }

    void 'teapot'() {
        expect:
            gru.test {
                get('/moon/teapot') {
                    executes controller.&teapot
                }
                expect {
                    status I_AM_A_TEAPOT
                }
            }
    }

    void 'redirect to teapot'() {
        expect:
            gru.test {
                get('/moon/redirectToTeapot') {
                    executes controller.&redirectToTeapot
                }
                expect {
                    redirect '/moon/teapot'
                }
            }
    }

    void 'forward to teapot'() {
        expect:
            gru.test {
                get('/moon/forwardToTeapot') {
                    executes controller.&forwardToTeapot
                }
                expect {
                    forward '/moon/teapot'
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
