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

    void 'render moons'() {
        expect:
            gru.test {
                get '/json'
                expect {
                    status NON_AUTHORITATIVE_INFORMATION
                    headers foo: 'bar', 'Content-Type': 'x-application/moons'
                    json([[name: "Moon", planet: "Earth"]])
                }
            }
    }

    void 'render moon'() {
        expect:
            gru.test {
                get '/json/moon'
                expect {
                    json name: "Moon", planet: "Earth"
                }
            }
    }

    void 'render moon (using converter)'() {
        expect:
            gru.test {
                get '/json/moon', {
                    executes controller.&show
                    param 'manual', 'true'
                }
                expect {
                    json 'moon.json'
                }
            }
    }

    void 'render with missing template'() {
        when:
            gru.test {
                get '/json/missing'
                expect {
                    json 'moon.json'
                }
            }.verify()
        then:
            AssertionError error = thrown(AssertionError)
            error.toString().contains('No view or template found for URI /json/missing')
    }

}
