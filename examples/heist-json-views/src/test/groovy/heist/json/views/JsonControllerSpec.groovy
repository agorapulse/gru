package heist.json.views

import com.agorapulse.gru.Gru
import com.agorapulse.gru.grails.Grails
import grails.testing.web.controllers.ControllerUnitTest
import grails.views.json.test.JsonViewUnitTest
import org.junit.Rule
import spock.lang.Specification

class JsonControllerSpec extends Specification implements ControllerUnitTest<JsonController> {

    @Rule Gru gru = Gru.equip(Grails.steal(this)).prepare {
        include UrlMappings
    }

    void 'render moon'() {
        expect:
            gru.test {
                get '/json'
                expect {
                    status NON_AUTHORITATIVE_INFORMATION
                    headers foo: 'bar'
                    json 'jsonViewResponse.json'
                }
            }
    }

}
