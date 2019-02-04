package com.agorapulse.gru.agp

import spock.lang.Specification

/**
 * Tests for API Gateway Proxy Request.
 */
class ApiGatewayProxyRequestSpec extends Specification {

    public static final String BASE_URI = '/context/'
    public static final String PATH = '/path'
    public static final String METHOD = 'POST'
    public static final String JSON = '{ "foo" : "bar" }'

    @SuppressWarnings('LineLength')
    void 'sanity check'() {
        when:
            ApiGatewayProxyRequest request =  new ApiGatewayProxyRequest(
                baseUri: BASE_URI,
                uri: PATH,
                method: METHOD,
                json: JSON,
                pathParameters: [id: '1']
            )

            request.addHeader('Auth', 'Bearer TeddyTheBear')
            request.addHeader('Content-Type', 'application/json')

            request.addParameter('max', 8)
            request.addParameter('offset', 5)

        then:
            with (request) {
                baseUri == BASE_URI
                uri == PATH
                method == METHOD

                context

                toJson() == '{"path":"/context/path","body":"{ \\"foo\\" : \\"bar\\" }","headers":{"Auth":"Bearer TeddyTheBear","Content-Type":"application/json"},"httpMethod":"POST","queryStringParameters":{"max":"8","offset":"5"},"pathParameters":{"id":"1"}}'
            }

        when:
            ApiGatewayConfiguration.MappingConfiguration mappingConfiguration = new ApiGatewayConfiguration.MappingConfiguration()
            mappingConfiguration.pathParameters('id')
            mappingConfiguration.queryStringParameters('offset', 'max')
        then:
            request.toJson(mappingConfiguration) == '{"id":"1","offset":"5","max":"8","foo":"bar"}'
    }

}
