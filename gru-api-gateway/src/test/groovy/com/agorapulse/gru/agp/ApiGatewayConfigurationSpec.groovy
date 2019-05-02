package com.agorapulse.gru.agp

import com.agorapulse.gru.Gru
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonOutput
import spock.lang.Specification

/**
 * Tests for API Gateway configuration.
 */
class ApiGatewayConfigurationSpec extends Specification {

    @SuppressWarnings('UnusedPrivateField')
    private final NonProxyHandler nonProxyHandler = new NonProxyHandler()

    void 'sample handler'() {
        given:
            // tag::simple[]
            Gru gru = Gru.equip(ApiGatewayProxy.steal(this) {
                map '/one' to SampleHandler.name                                // <1>
                map '/two' to SampleHandler                                     // <2>
                map '/three', GET to SampleHandler.name                         // <3>
                map '/four', GET, POST to SampleHandler
            })
            // end::simple[]
        when:
            gru.test {
                get '/one'
                expect {
                    json 'firstResponse.json'
                }
            }
        then:
            gru.verify()

        when:
            gru.reset().test {
                get '/two'
                expect {
                    json 'secondResponse.json'
                }
            }
        then:
            gru.verify()

        when:
            gru.reset().test {
                get '/three'
                expect {
                    json 'thirdResponse.json'
                }
            }
        then:
            gru.verify()

        when:
            gru.reset().test {
                get '/four'
                expect {
                    json 'fourthResponse.json'
                }
            }
        then:
            gru.verify()
    }

    void 'generic handler'() {
        given:
            Gru gru = Gru.equip(ApiGatewayProxy.steal(this) {
                map '/generic' to GenericHandler
            })
        when:
            gru.reset().test {
                get '/generic'
            }.verify()
        then:
            AssertionError error = thrown(AssertionError)
            error.cause instanceof IllegalArgumentException
    }

    void 'no zero arg constructor handler'() {
        given:
            Gru gru = Gru.equip(ApiGatewayProxy.steal(this) {
                map '/no-zero-arg' to NoZeroArgConstructorHandler
            })
        when:
            gru.reset().test {
                get '/no-zero-arg'
            }.verify()
        then:
            AssertionError error = thrown(AssertionError)
            error.cause instanceof IllegalStateException
    }

    void 'route not mapped'() {
        given:
            Gru gru = Gru.equip(ApiGatewayProxy.steal(this) { })
        when:
            gru.reset().test {
                get '/not-found'
            }.verify()
        then:
            AssertionError error = thrown(AssertionError)
            error.cause instanceof IllegalArgumentException
    }

    void 'non-proxy handler'() {
        given:
            // tag::noproxy[]
            Gru gru = Gru.equip(ApiGatewayProxy.steal(this) {
                map '/five/{action}/{id}' to NonProxyHandler, {                 // <1>
                    pathParameters('action', 'id')                                  // <2>
                    queryStringParameters 'foo', 'bar'                              // <3>
                    response(200) {                                                     // <4>
                        headers 'Content-Type': 'application/json'                              // <5>
                    }
                }
            })
            // end::noproxy[]
        when:
            gru.test {
                get '/five/hello/world', {
                    params foo: 'oof', bar: 'rab', baz: 'zab'
                }
                expect {
                    json 'fiveResponse.json'
                }
            }
        then:
            gru.verify()
    }

    void 'proxy to non-existing class'() {
        given:
            Gru gru = Gru.equip(ApiGatewayProxy.steal(this) {
                map '/ten' to 'bing.bang.Bong'
            })
        when:
            gru.reset().test {
                get '/ten'
            }.verify()
        then:
            AssertionError error = thrown(AssertionError)
            error.cause instanceof ClassNotFoundException
    }

    void 'proxy to streaming'() {
        given:
            Gru gru = Gru.equip(ApiGatewayProxy.steal(this) {
                map '/six' to StreamingHandler
            })
        when:
            gru.reset().test {
                get '/six'
                expect {
                    json 'sixthResponse.json'
                }
            }
        then:
            gru.verify()
    }

}

class SampleHandler implements RequestHandler<Map, Map> {
    @Override Map handleRequest(Map input, Context context) {
        return [body: JsonOutput.toJson(input: input)]
    }
}
class NonProxyHandler implements RequestHandler<Map, Map> {
    @Override Map handleRequest(Map input, Context context) {
        return [input: input]
    }
}

class GenericHandler<I, O> implements RequestHandler<I, O> {
    @Override O handleRequest(I input, Context context) {
        throw new UnsupportedOperationException()
    }
}

class NoZeroArgConstructorHandler  implements RequestHandler<Map, Map> {

    NoZeroArgConstructorHandler(String foo, String bar) { }

    @Override Map handleRequest(Map input, Context context) {
        throw new UnsupportedOperationException()
    }
}

class StreamingHandler implements RequestStreamHandler {
    @Override
    void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        ObjectMapper mapper = new ObjectMapper()
        mapper.writer().writeValue(output, [body: [input: mapper.readValue(input, Map)]])
    }
}
