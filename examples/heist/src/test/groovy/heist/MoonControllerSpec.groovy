package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.grails.Grails
import com.agorapulse.gru.grails.minions.GrailsHtmlMinion
import com.agorapulse.gru.grails.minions.InterceptorsMinion
import com.agorapulse.gru.grails.minions.ModelMinion
import com.agorapulse.gru.jsonunit.MatchesPattern
import grails.testing.web.controllers.ControllerUnitTest
import org.junit.Rule
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.ModelAndView
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
class MoonControllerSpec extends Specification implements ControllerUnitTest<MoonController> {

    @Rule Gru<Grails<MoonControllerSpec>> gru = Gru.equip(Grails.steal(this)).prepare {
        include UrlMappings
    }

    void setup() {
        controller.moonService = new MoonService()
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

    void 'good interceptor'() {
        expect:
            gru.test {
                include GoodInterceptor
                get '/moons/good/interceptor'
                expect {
                    status NOT_FOUND
                    headers 'X-Good-Message': 'You are so good!'
                }
            }
    }

    void 'bad interceptor'() {
        expect:
            gru.test {
                include BadInterceptor
                get '/moons/bad/interceptor'
            }
    }

    void 'ugly interceptor'() {
        expect:
            gru.test {
                include UglyInterceptor
                get '/moons/ugly/interceptor'
            }
    }

    void 'with interceptor minion'() {
        expect:
            gru.engage(new InterceptorsMinion()).test {
                get '/moons/earth/moon'
            }
    }

    void 'give me some model'() {
        expect:
            gru.test {
                get '/moons/give-me-some-model', {
                    executes controller.&modelAndView
                }
                expect {
                    model new ModelAndView('info', [foo: 'bar'], HttpStatus.ACCEPTED)
                }
            }
    }

    void 'give me anything'() {
        expect:
            gru.test {
                get '/moons/give-me-anything', {
                    executes controller.&anything
                }
                expect {
                    model(['foo', 'bar'])
                }
            }
    }

    // tag::stealWithShrinkRay[]
    void 'steal the moon with shrink ray'() {
        expect:
            gru.test {
                delete '/moons/earth/moon', {
                    params with: 'shrink-ray'
                }
                expect {
                    status NO_CONTENT
                }
            }
    }
    // end::stealWithShrinkRay[]

    // tag::secretMoon[]
    void 'visit secret moon Noom'() {
        expect:
            gru.test {
                get '/moons/earth/noom', {
                    headers Authorization: 'Felonius'
                }
            }
    }
    // end::secretMoon[]

    // tag::newMoon[]
    void 'create moon for Margot'() {
        expect:
            gru.test {
                post '/moons/earth', {
                    json 'newMoonRequest.json'
                }
            }
    }
    // end::newMoon[]

    // tag::jsonHeaders[]
    void 'json is rendered'() {
        expect:
            gru.test {
                get '/moons/earth/moon'
                expect {
                    headers 'Content-Type': 'application/json;charset=UTF-8'
                }
            }
    }
    // end::jsonHeaders[]

    // tag::verifyJson[]
    void 'verify json'() {
        expect:
            gru.test {
                get '/moons/earth/moon'
                expect {
                    json 'moonResponse.json'
                }
            }
    }
    // end::verifyJson[]


    // tag::verifyJson2[]
    void 'verify json 2'() {
        expect:
            gru.test {
                get '/moons/earth'
                expect {
                    json 'moonsResponse.json', IGNORING_EXTRA_ARRAY_ITEMS
                }
            }
    }
    // end::verifyJson2[]

    // tag::customiseJsonUnit[]
    void 'customise json unit'() {
        expect:
            gru.test {
                get '/moons/earth/moon'
                expect {
                    json 'moonResponse.json'
                    json {
                        withTolerance(0.1).withMatcher(
                            'negativeIntegerString',
                            MatchesPattern.matchesPattern(/-\d+/)
                        )
                    }
                }
            }
    }
    // end::customiseJsonUnit[]

    // tag::redirect[]
    void 'no planet needed'() {
        expect:
            gru.test {
                get '/moons/-/moon'
                expect {
                    redirect '/moons/earth/moon'
                }
            }
    }
    // end::redirect[]

    // tag::verifyAction[]
    void 'verify action'() {
        expect:
            gru.test {
                get '/moons/earth/moon', {
                    executes controller.&moon
                }
            }
    }
    // end::verifyAction[]

    // tag::model[]
    void 'verify model'() {
        given:
            def moon = [name: 'Moon', planet: 'Earth']
            MoonService moonService = Mock(MoonService)
            controller.moonService = moonService
        when:
            gru.test {
                get '/moons/earth/moon/info'
                expect {
                    model moon: moon
                }
            }
        then:
            gru.verify()
            1 * moonService.findByPlanetAndName('earth', 'moon') >> moon
    }
    // end::model[]

    // tag::modelEngage[]
    void 'verify model with engage'() {
        given:
            def moon = [name: 'Moon', planet: 'Earth']
            MoonService moonService = Mock(MoonService)
            controller.moonService = moonService
        when:
            gru.engage(new ModelMinion(model: [moon: moon])).test {
                get '/moons/earth/moon/info'
            }
        then:
            gru.verify()
            1 * moonService.findByPlanetAndName('earth', 'moon') >> moon
    }
    // end::modelEngage[]

    // tag::forward[]
    void 'verify forward'() {
        expect:
            gru.test {
                get '/moons/earth/moon', {
                    params info: 'true'
                }
                expect {
                    forward '/moons/earth/moon/info'
                }
            }
    }
    // end::forward[]

    // tag::verifyHtml[]
    void 'verify html'() {
        expect:
            gru.test {
                get '/moons/earth/moon/info'
                expect {
                    html 'htmlResponse.html'
                }
            }
    }
    // end::verifyHtml[]

    // tag::verifyText[]
    void 'verify text'() {
        expect:
            gru.test {
                get '/moons/earth/moon/info', {
                    headers 'Accept': 'text/plain'
                }
                expect {
                    text 'textResponse.txt'
                }
            }
    }
    // end::verifyText[]
}
