package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.grails.Grails
import grails.testing.web.controllers.ControllerUnitTest
import org.junit.Rule
import spock.lang.Specification

class IncludeInterceptorSpec extends Specification implements ControllerUnitTest<MoonController> {

    @Rule Gru<Grails<IncludeInterceptorSpec>> gru = Gru.equip(Grails.steal(this)).prepare {
        include ApiUrlMappings
        include VectorInterceptor, true                                                 // <1>
    }

    void "moon is still there"() {
        given:
            defineBeans {
                vectorMessage(VectorMessage, "Le Vecteur était là!")                    // <2>
            }
        expect:
            gru.test {
                delete '/api/v1/moons/earth/moon'
                expect {
                    status NOT_FOUND
                    headers 'X-Message': "Le Vecteur était là!"                         // <3>
                }
            }
    }

}
