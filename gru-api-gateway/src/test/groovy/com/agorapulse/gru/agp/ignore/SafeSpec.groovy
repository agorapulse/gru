package com.agorapulse.gru.agp.ignore

import spock.lang.Specification

class SafeSpec extends Specification {

    void 'safe calls'() {
        expect:
            Safe.call { "bar" } == "bar"

        when:
            Safe.call { throw new IllegalArgumentException() }
        then:
            thrown(IllegalArgumentException)

        when:
            Safe.call { throw new IllegalAccessException() }
        then:
            IllegalStateException ise = thrown(IllegalStateException)
            ise.cause instanceof IllegalAccessException
    }

}
