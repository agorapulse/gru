package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.exception.GroovyAssertAwareMultipleFailureException
import com.agorapulse.gru.grails.Grails
import com.agorapulse.gru.grails.minions.GrailsHtmlMinion
import com.agorapulse.gru.grails.minions.InterceptorsMinion
import com.agorapulse.gru.grails.minions.ModelMinion
import com.agorapulse.gru.jsonunit.MatchesPattern
import com.agorapulse.gru.minions.Command
import com.agorapulse.gru.minions.HttpMinion
import grails.testing.web.controllers.ControllerUnitTest
import groovy.transform.NotYetImplemented
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

    void 'you shall not steal earth (not mapped)'() {
        when:
            gru.test {
                delete '/moons/earth'
            }.verify()
        then:
            AssertionError error = thrown(AssertionError)
            error.message == 'URL \'/moons/earth\' is not mapped with method DELETE!'
    }

    void 'render json'() {
        expect:
            gru.test {
                include DefaultUrlMappings
                get('/moon') {
                    executes controller.&index
                }
                expect {
                    json foo: 'bar'
                }
            }
    }

    void 'teapot'() {
        expect:
            gru.test {
                include DefaultUrlMappings
                get('/moon/teapot') {
                    executes controller.&teapot
                }
                expect {
                    status I_AM_A_TEAPOT
                }
            }
    }

    void 'TEAPOT is not OK'() {
        when:
            gru.test {
                include DefaultUrlMappings
                get ('/moon/teapot') {
                    executes controller.&teapot
                }
            }.verify()
        then:
            thrown(AssertionError)
    }

    void 'redirect to teapot'() {
        expect:
            gru.test {
                include DefaultUrlMappings
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
                include DefaultUrlMappings
                get('/moon/forwardToTeapot') {
                    executes controller.&forwardToTeapot
                }
                expect {
                    forward '/moon/teapot?format='
                }
            }
    }

    void 'echo'() {
        expect:
            gru.test {
                include DefaultUrlMappings
                post '/moon/echo', {
                    json 'echoRequest.json'
                }
                expect {
                    json 'echoResponse.json'
                }
            }
    }

    void 'echo with generic content call'() {
        expect:
            gru.test {
                include DefaultUrlMappings
                post '/moon/echo', {
                    content 'echoRequest.json', 'application/json'
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

    @NotYetImplemented
    void 'good interceptor with exception'() {
        expect:
            gru.test {
                include GoodInterceptor
                get '/moons/error'
                expect {
                    status INTERNAL_SERVER_ERROR
                    headers 'X-Good-Message': 'You are so good!'
                }
            }
    }

    void 'bad interceptor'() {
        expect:
            gru.test {
                include BadInterceptor
                get '/moons/bad/interceptor'
                expect {
                    status NOT_FOUND
                }
            }
    }

    void 'ugly interceptor'() {
        expect:
            gru.test {
                include UglyInterceptor
                get '/moons/ugly/interceptor'
                expect {
                    status NOT_FOUND
                }
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

    void 'test multiple exceptions'() {
        when:
            gru.test {
                get '/moons/earth/moon'
                expect {
                    status I_AM_A_TEAPOT
                    text 'textResponse.txt'
                    json 'echoRequest.json'
                    html 'htmlResponse.html'
                }
            }
        and:
            gru.verify()
        then:
            GroovyAssertAwareMultipleFailureException ex = thrown(GroovyAssertAwareMultipleFailureException)
            ex.message.contains('assert client.response.status == status')
            ex.message.contains('assert actual == expected')
        when:
            assert !gru.verify()
        then:
            noExceptionThrown()
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

    void 'verify without test'() {
        when:
            gru.verify()
        then:
            AssertionError e = thrown(AssertionError)
            e.message == 'There are no expectations!'
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

    void 'failed to visit secret moon Noom'() {
        expect:
            gru.test {
                get '/moons/earth/noom', {
                    headers Authorization: 'Vector'
                }
                expect {
                    status FORBIDDEN
                }
            }
    }

    void 'failed to visit secret non-exiting moon'() {
        expect:
            gru.test {
                get '/moons/earth/mesicek'
                expect {
                    status NOT_FOUND
                }
            }
    }

    // tag::newMoon[]
    void 'create moon for Margot'() {
        expect:
            gru.test {
                post '/moons/earth', {
                    json(name: 'Margot')
                }
            }
    }
    // end::newMoon[]

    // tag::newMoonGeneric[]
    void 'create moon for Margot generic'() {
        expect:
            gru.test {
                post '/moons/earth', {
                    content 'newMoonRequest.json', 'application/json'
                }
            }
    }
    // end::newMoonGeneric[]

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

    // tag::verifyTextInline[]
    void 'verify text inline'() {
        expect:
            gru.test {
                get '/moons/earth/moon/info', {
                    headers 'Accept': 'text/plain'
                }
                expect {
                    text inline('Moon goes around Earth')
                }
            }
    }
    // end::verifyTextInline[]

    void 'add wrong artifact'() {
        when:
            Gru.equip(Grails.steal(this)).test {
                include MoonControllerSpec
                get '/moons/earth/moon'
            }.verify()
        then:
            thrown(IllegalArgumentException)
    }

    void 'pass wrong closure'() {
        when:
            Gru.equip(Grails.steal(this)).test {
                get '/moons/earth/moon', {
                    executes {}
                }
            }.verify()
        then:
            thrown(IllegalArgumentException)
    }

    void 'there is no charon around earth'() {
        expect:
            gru.test {
                get '/moons/earth/charon'
                expect {
                    status NOT_FOUND
                    text 'emptyResponse.txt'
                }
            }
    }

    void 'there is no charon around earth (using command)'() {
        expect:
            gru.test {
                command (HttpMinion, new Command<HttpMinion>() {
                    @Override
                    void execute(HttpMinion minion) {
                        minion.setStatus(404)
                    }
                })
                get '/moons/earth/charon/info'
                expect {
                    text 'emptyResponse.txt'
                }
            }
    }

    void 'controller not mocked'() {
        when:
            gru.test {
                include DefaultUrlMappings
                get '/foo/bar'
            }.verify()
        then:
            AssertionError error = thrown(AssertionError)
            error.message == 'The URL is not mapped or the controller \'foo\' is not mocked!'
    }

    void 'you shall not steal earth'() {
        when:
            gru.test {
                include DefaultUrlMappings
                delete '/moon/earth'
            }.verify()
        then:
            AssertionError error = thrown(AssertionError)
            error.message == 'Action \'earth\' does not exist in controller \'moon\''
    }

    void 'url mapping to wrong action'() {
        when:
            gru.test {
                get '/moons/earth/moon', {
                    executes controller.&steal
                }
            }.verify()
        then:
            AssertionError error = thrown(AssertionError)
            error.message.startsWith 'GET: \'/moons/earth/moon\' is not mapped to heist.MoonController'
            error.message.endsWith '.steal!'
    }

    // tag::fileUpload[]
    void 'upload file with message'() {
        expect:
            gru.test {
                post '/moons/upload', {
                    upload {                                                            // <1>
                        params message: 'Hello'                                         // <2>
                        file 'theFile', 'hello.txt', inline('Hello World'), 'text/plain'// <3>
                    }
                    // Grails only - required because of bug in mock request
                    executes controller.&postWithMessageAndImage                        // <4>
                }
                expect {
                    json 'uploadResult.json'
                }
            }
    }
    // end::fileUpload[]
}
