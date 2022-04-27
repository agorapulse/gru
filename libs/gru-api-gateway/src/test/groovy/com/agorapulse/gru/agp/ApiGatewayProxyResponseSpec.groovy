/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2022 Agorapulse.
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
package com.agorapulse.gru.agp

import groovy.json.JsonOutput
import spock.lang.Specification

/**
 * Tests for API Gateway Proxy Response.
 */
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
