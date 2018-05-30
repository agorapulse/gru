package com.agorapulse.gru.http

import spock.lang.Specification
import spock.lang.Unroll

class GruHttpRequestSpec extends Specification {

    @Unroll
    void '#baseUri plus #uri is normalized to #url'() {
        given:
            GruHttpRequest request = new GruHttpRequest(
                baseUri: baseUri,
                uri: uri
            )
        expect:
            request.buildOkHttpRequest().url().toString() == url
        where:
            baseUri                     | uri                               | url
            'http://localhost:8080'     | '/hello'                          | 'http://localhost:8080/hello'
            'http://localhost:8080/'    | '/hello'                          | 'http://localhost:8080/hello'
            'http://localhost:8080/'    | 'hello'                           | 'http://localhost:8080/hello'
            'http://localhost:8080'     | 'hello'                           | 'http://localhost:8080/hello'
            null                        | 'http://localhost:8080/hello'     | 'http://localhost:8080/hello'
    }

}
