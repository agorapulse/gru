package com.agorapulse.gru.spring.heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.spring.Spring
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest
class MoonControllerSpec extends Specification {

    @Autowired MockMvc mvc

    @Rule Gru<Spring> gru = Gru.equip(Spring.steal(this))

    // tag::mockmvc[]
    void 'json is rendered'() {
        expect:
            gru.test {
                get '/moons/earth/moon', {
                    request {                                                               // <1>
                        accept(MediaType.APPLICATION_JSON_UTF8)                             // <2>
                    }
                    and {                                                                   // <3>
                        locale(Locale.CANADA)
                    }
                }
                expect {
                    headers 'Content-Type': 'application/json;charset=UTF-8'
                    json 'moonResponse.json'
                    that content().encoding('UTF-8')                        // <4>
                    and content().contentType(MediaType.APPLICATION_JSON_UTF8)              // <5>
                }
            }
    }
    // end::mockmvc[]

    void 'the only true moon'() {
        expect:
            gru.test {
                get '/moons/the-only-true-moon'
                expect {
                    redirect '/moons/earth/moon'
                }
            }
    }

    void 'params echo'() {
        expect:
            gru.test {
                get('/moons/params-echo') {
                    params foo: 'bar', zoo: 'fun'
                }
                expect {
                    json 'paramsEchoResponse.json'
                }
            }
    }

    void 'params echo multiple'() {
        expect:
            gru.test {
                get('/moons/params-echo') {
                    params foo: 'bar'
                    params zoo: 'fun'
                }
                expect {
                    json 'paramsEchoResponse.json'
                }
            }
    }

    void 'render html'() {
        expect:
            gru.test {
                get('/moons/greeting') {
                    params name: 'Gru'
                }
                expect {
                    html 'greetingsResponse.html'
                }
            }
    }

    void 'upload file with message'() {
        expect:
            gru.test {
                post '/moons/upload', {
                    upload {
                        params message: 'Hello'
                        file 'theFile', 'hello.txt', inline('Hello World'), 'text/plain'
                    }
                }
                expect {
                    json 'uploadResult.json'
                }
            }
    }

    void 'send cookies'() {
        expect:
            gru.test {
                get '/moons/cookie', {
                    cookies chocolate: 'rules'
                }
                expect {
                    json 'cookies.json', IGNORING_EXTRA_FIELDS
                }
            }
    }

}
