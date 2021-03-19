package com.agorapulse.gru

import spock.lang.Specification

class LazyClientSpec extends Specification {

    Client mockClient = Mock()

    int created

    void 'lazy client initialized only once and delegates to delegate'() {
        when:
            Client lazy = LazyClient.create {
                created++
                mockClient
            }
        then:
            created == 0

        when:
            lazy.request
        then:
            created == 1

            1 * mockClient.request

        when:
            lazy.response
        then:
            created == 1

            1 * mockClient.response

        when:
            lazy.reset()
        then:
            created == 1

            1 * mockClient.reset()

        when:
            lazy.run(null, null)
        then:
            created == 1

            1 * mockClient.run(null, null)

        when:
            lazy.loadFixture(null)
        then:
            created == 1

            1 * mockClient.loadFixture(null)

        when:
            lazy.currentDescription
        then:
            created == 1

            1 * mockClient.currentDescription

        when:
            lazy.initialSquad
        then:
            created == 1

            1 * mockClient.initialSquad

        when:
            lazy.unitTest
        then:
            created == 1

            1 * mockClient.unitTest

    }

}
