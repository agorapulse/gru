package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.grails.Grails
import grails.testing.web.controllers.ControllerUnitTest
import org.junit.Rule
import spock.lang.Specification

import static com.agorapulse.gru.Gru.equip
import static com.agorapulse.gru.grails.Grails.steal

class SplitSpec extends Specification
    implements ControllerUnitTest<MoonController> {

    @Rule Gru<Grails<SplitSpec>> gru = equip(steal(this)).prepare {
        include UrlMappings
    }

    // tag::whenThen[]
    void 'look at the moon'() {
        given:
            MoonService moonService = Mock(MoonService)
            controller.moonService = moonService                                            // <1>
        when:
            gru.test {                                                                      // <2>
                get '/moons/earth/moon'
            }
        then:
            gru.verify()                                                                    // <3>
            1 * moonService.findByPlanetAndName("earth", "moon") >> [name: "Moon"]          // <4>
    }
    // end::whenThen[]
}
