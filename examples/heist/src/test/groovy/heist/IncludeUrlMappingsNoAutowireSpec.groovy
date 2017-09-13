package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.grails.Grails
import grails.testing.web.controllers.ControllerUnitTest
import org.junit.Rule
import spock.lang.Specification

class IncludeUrlMappingsNoAutowireSpec extends Specification implements ControllerUnitTest<MoonController> {

    void "cannot autowire url mappings"() {
        when:
            Gru.equip(Grails.steal(this)).prepare {
                include ApiUrlMappings, true
            }. test {
                get '/api/moons/earth/moon'
            }.verify()
        then:
            thrown(IllegalArgumentException)
    }
}
