package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.grails.Grails
import grails.testing.web.controllers.ControllerUnitTest
import org.junit.Rule
import spock.lang.Specification

class IncludeUrlMappingsSpec extends Specification implements ControllerUnitTest<MoonController> {

    @Rule Gru<Grails<IncludeUrlMappingsSpec>> gru = Gru.equip(Grails.steal(this)).prepare {
        include ApiUrlMappings                                                          // <1>
    }

    void "moon is still there"() {
        expect:
            gru.test {
                delete '/api/v1/moons/earth/moon'                                       // <2>
            }
    }
}
