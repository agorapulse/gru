/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2024 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agorapulse.gru.spring.heist

import com.agorapulse.gru.Gru
import com.agorapulse.gru.spring.Spring
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.AutoCleanup
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest
class MoonControllerSpec extends Specification {

    @Autowired MockMvc mvc

    Gru gru = Gru.create(Spring.create(this))

    // tag::mockmvc[]
    void 'json is rendered'() {
        expect:
            gru.test {
                get '/moons/earth/moon', {
                    request {                                                           // <1>
                        accept(MediaType.APPLICATION_JSON_UTF8)                         // <2>
                    }
                    and {                                                               // <3>
                        locale(Locale.CANADA)
                    }
                }
                expect {
                    headers 'Content-Type': 'application/json;charset=UTF-8'
                    json 'moonResponse.json'
                    that content().encoding('UTF-8')                                    // <4>
                    and content().contentType(MediaType.APPLICATION_JSON_UTF8)          // <5>
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
                    cookie 'chocolate', 'rules'
                }
                expect {
                    json 'cookies.json', IGNORING_EXTRA_FIELDS
                }
            }
    }

    void 'set cookies'() {
        expect:
            gru.test {
                get '/moons/setCookie'
                expect {
                    cookies chocolate: 'rules'
                    cookie {
                        name 'coffee'
                        value 'lover'
                        secure true
                        domain 'localhost'
                    }
                }
            }
    }

}
