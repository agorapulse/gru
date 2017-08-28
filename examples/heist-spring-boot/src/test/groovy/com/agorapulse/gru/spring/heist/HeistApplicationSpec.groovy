package com.agorapulse.gru.spring.heist

import spock.lang.Specification

/**
 * Tests running the application
 */
class HeistApplicationSpec extends Specification {

    void 'test app'() {
        when:
            HeistApplication.main()
        then:
            HeistApplication.context
        when:
            HeistApplication.context.stop()
        then:
            noExceptionThrown()
    }
}
