/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2023 Agorapulse.
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
package com.agorapulse.gru.okhttp

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Tests for Gru Http Request.
 */
class GruHttpRequestSpec extends Specification {

    @Unroll
    void '#baseUri plus #uri is normalized to #url'() {
        given:
            GruOkHttpRequest request = new GruOkHttpRequest(
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
