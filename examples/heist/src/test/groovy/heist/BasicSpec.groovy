package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.grails.Grails
import grails.testing.web.controllers.ControllerUnitTest
import org.junit.Rule
import spock.lang.Specification

class BasicSpec extends Specification implements ControllerUnitTest<MoonController> {   // <1>

    @Rule Gru<Grails<BasicSpec>> gru = Gru.equip(Grails.steal(this)).prepare {          // <2>
        include UrlMappings                                                             // <3>
    }

    void 'look at the moon'() {
        expect:
            gru.test {
                get '/moons/earth/moon'                                                  // <4>
            }
    }

    void setup() {
        controller.moonService = new MoonService()
    }
}
