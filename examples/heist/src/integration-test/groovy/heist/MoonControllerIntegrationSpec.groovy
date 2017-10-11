package heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.grails.minions.InterceptorsMinion
import com.agorapulse.gru.grails.minions.ModelMinion
import com.agorapulse.gru.http.Http
import com.agorapulse.gru.jsonunit.MatchesPattern
import grails.testing.mixin.integration.Integration
import org.junit.Rule
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.ModelAndView
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@Integration
class MoonControllerIntegrationSpec extends Specification {

    // tag::integrationSetup[]
    @Value('${local.server.port}')
    Integer serverPort                                                                      // <1>

    @Rule Gru<Http> gru = Gru.equip(Http.steal(this))                                       // <2>

    void setup() {
        final String serverUrl = "http://localhost:${serverPort}"                           // <3>
        gru.prepare {
            baseUri serverUrl                                                               // <4>
        }
    }

    // end::integrationSetup[]

    void 'render json'() {
        expect:
            gru.test {
                get('/moon')
                expect {
                    json 'indexResponse.json'
                }
            }
    }

    void 'teapot'() {
        expect:
            gru.test {
                get('/moon/teapot')
                expect {
                    status I_AM_A_TEAPOT
                }
            }
    }

    void 'redirect to teapot'() {
        expect:
            gru.test {
                get('/moon/redirectToTeapot')
                expect {
                    redirect '/moon/teapot'
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

    void 'verify html'() {
        expect:
            gru.test {
                get '/moons/earth/moon/info'
                expect {
                    html 'htmlResponse.html'
                }
            }
    }

    void 'good interceptor'() {
        expect:
            gru.test {
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
                get '/moons/bad/interceptor'
                expect {
                    status NOT_FOUND
                }
            }
    }

    void 'ugly interceptor'() {
        expect:
            gru.test {
                get '/moons/ugly/interceptor'
                expect {
                    status NOT_FOUND
                }
            }
    }

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

    void 'visit secret moon Noom'() {
        expect:
            gru.test {
                get '/moons/earth/noom', {
                    headers Authorization: 'Felonius'
                }
            }
    }

    void 'create moon for Margot'() {
        expect:
            gru.test {
                post '/moons/earth', {
                    json 'newMoonRequest.json'
                }
            }
    }

    void 'json is rendered'() {
        expect:
            gru.test {
                get '/moons/earth/moon'
                expect {
                    headers 'Content-Type': 'application/json;charset=UTF-8'
                }
            }
    }

    void 'verify json'() {
        expect:
            gru.test {
                get '/moons/earth/moon'
                expect {
                    json 'moonResponse.json'
                }
            }
    }

    void 'verify json 2'() {
        expect:
            gru.test {
                get '/moons/earth'
                expect {
                    json 'moonsResponse.json', IGNORING_EXTRA_ARRAY_ITEMS
                }
            }
    }

    void 'no planet needed'() {
        expect:
            gru.test {
                get '/moons/-/moon'
                expect {
                    redirect '/moons/earth/moon'
                }
            }
    }

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

    void 'params to json'() {
        expect:
            gru.test {
                post '/moons/params-to-json', {
                    params foo: 'bar', one: 'un'
                }
                expect {
                    json 'paramsToJson.json'
                }
            }
    }
}
