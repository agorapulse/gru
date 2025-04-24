/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2025 Agorapulse.
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
package com.agorapulse.gru.http

import com.agorapulse.gru.Gru
import com.stehno.ersatz.ContentType
import com.stehno.ersatz.ErsatzServer
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

class HttpRedirectSpec extends Specification {

    @AutoCleanup @Shared ErsatzServer server = new ErsatzServer({
        autoStart(true)
    })

    @Shared Gru gru = Gru.create(Http.create(this))

    void setup() {
        server.expectations {
            get('/test') {
                responds()
                    .cookie('Test-Cookie', 'true')
                    .header('Location', '/test/1')
                    .code(303)
                    .body('')

            }
            get('/test/1') {
                responds()
                    .code(200)
                    .body('{"message":"OK"}', ContentType.APPLICATION_JSON)
            }
        }

        gru.prepare(server.httpUrl('/'))
    }

    void 'expect cookie from original response when redirecting'() {
        expect:
        gru.test {
            get('/test')
            expect {
                status SEE_OTHER
                cookie 'Test-Cookie', 'true'
            }
        }
    }

    void 'expect header from original response when redirecting'() {
        expect:
        gru.test {
            get('/test')
            expect {
                status SEE_OTHER
                header 'Location', '/test/1'
            }
        }
    }
}
