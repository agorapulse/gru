package com.agorapulse.gru.agp

import groovy.json.JsonOutput
import spock.lang.Specification

class ApiGatewayProxyResponseSpec extends Specification {


    public static final String BODY = 'mock body'
    public static final int STATUS = 201
    public static final String REDIRECT_URL = 'http://www.example.com'

    void 'response parsed properly'() {
        when:
            ApiGatewayProxyResponse response = new ApiGatewayProxyResponse(
                JsonOutput.toJson(
                    body: BODY,
                    headers: [foo: 'bar'],
                    statusCode: STATUS
                )
            )
        then:
            response.text == BODY
            response.status == STATUS
            response.getHeaders('foo') == ['bar']
            response.getHeaders('bar').empty
            response.redirectUrl == null
    }

    void 'understands redirect'() {
        when:
            ApiGatewayProxyResponse response = new ApiGatewayProxyResponse(
                JsonOutput.toJson(
                    headers: [Location: REDIRECT_URL],
                    statusCode: 301
                )
            )
        then:
            response.redirectUrl == REDIRECT_URL
    }

}
