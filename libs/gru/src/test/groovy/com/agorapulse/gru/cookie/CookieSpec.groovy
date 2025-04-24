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
package com.agorapulse.gru.cookie

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Basic tests for Cookie.
 */
class CookieSpec extends Specification {

    @Unroll
    void 'cookie #cookie is exported as #text and can be parsed back'() {
        when:
            String exported = cookie
        then:
            exported == text

        when:
            Cookie parsed = Cookie.parse(exported)
        then:
            parsed.similarTo(cookie)

        where:
            text                                                                | cookie
            'name=value; path=/'                                                | build { name 'name' value 'value' }
            'name=value; domain=example.com; path=/'                            | build { name 'name' value 'value' domain 'example.com' }
            'name=value; domain=example.com; path=/; httponly'                  | build { name 'name' value 'value' domain 'example.com' httpOnly true }
            'name=value; expires=Wed, 18 May 2033 03:33:20 GMT; path=/; secure' | build { name 'name' value 'value' secure true expiresAt 2000000000000 }
    }

    @Unroll
    void 'cookie #cookie is not similar to #reference'() {
        expect:
            !reference.similarTo(cookie)
            !cookie.similarTo(reference)
        where:
            reference = Cookie.parse('a=b; Path=/c; Domain=example.com; Max-Age=5; Secure; HttpOnly')
            cookie << [
                build { name 'c' },
                build { name 'a' value 'c' },
                build { name 'a' value 'b' path '/a' },
                build { name 'a' value 'b' path '/c' domain 'test.org' },
                build { name 'a' value 'b' path '/c' domain 'example.com' },
                build { name 'a' value 'b' path '/c' domain 'example.com' secure true },
            ]
    }

    private static Cookie build(
        @DelegatesTo(value = Cookie.Builder, strategy = Closure.DELEGATE_FIRST)
        Closure<Cookie.Builder> builder
    ) {
        Cookie.Builder b = new Cookie.Builder()
        b.with builder
        return b.build()
    }

}
