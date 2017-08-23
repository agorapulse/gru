package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.grails.Grails
import grails.testing.web.controllers.ControllerUnitTest
import org.junit.Rule
import spock.lang.Specification

class IncludeInterceptorSpec extends Specification implements ControllerUnitTest<MoonController> {

    @Rule Gru<Grails<IncludeInterceptorSpec>> gru = Gru.equip(Grails.steal(this)).prepare {
        include ApiUrlMappings
        include VectorInterceptor                                                       // <1>
    }

    void "moon is still there"() {
        expect:
            gru.test {
                delete '/api/v1/moons/earth/moon'
                expect {                                                                // <2>
                    status NOT_FOUND
                    headers 'X-Message': "Vector was here"
                }
            }
    }

}
