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

    private static Cookie build(
        @DelegatesTo(value = Cookie.Builder, strategy = Closure.DELEGATE_FIRST)
        Closure<Cookie.Builder> builder
    ) {
        Cookie.Builder b = new Cookie.Builder()
        b.with builder
        return b.build()
    }

}
