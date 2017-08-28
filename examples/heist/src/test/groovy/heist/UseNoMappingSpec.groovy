package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.grails.Grails
import com.agorapulse.gru.minions.HttpMinion
import grails.testing.web.controllers.ControllerUnitTest
import org.junit.Rule
import spock.lang.Specification

class UseNoMappingSpec extends Specification implements ControllerUnitTest<MoonController> {

    @Rule Gru<Grails<UseNoMappingSpec>> gru = Gru.equip(Grails.steal(this))

    void 'look at the moon'() {
        when:
            gru.test {
                command(HttpMinion) {
                    baseUri '/moons'
                }
                get '/earth/moon'
            }.verify()
        then:
            AssertionError ex = thrown(AssertionError)
            ex.message == 'URI for action is specified but UrlMappings is not defined nor default UrlMappings class exists!'
    }

    void setup() {
        controller.moonService = new MoonService()
    }
}
