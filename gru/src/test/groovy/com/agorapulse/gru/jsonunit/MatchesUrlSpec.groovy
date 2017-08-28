package com.agorapulse.gru.jsonunit

import org.hamcrest.Description
import org.hamcrest.StringDescription
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Tests url matcher.
 */
@Unroll class MatchesUrlSpec extends Specification {

    void 'url #url #matchesDesc'() {
        expect:
            MatchesUrl.INSTANCE.matches(url) == matches

        where:
            matches | url
            true    | 'https://www.google.com'
            true    | 'http://www.example.com'
            false   | 'foo.bar'
            false   | null

            matchesDesc = matches ? 'matches' : 'does not match'

    }

    void 'test description'() {
        given:
            Description description = new StringDescription()
        when:
            MatchesUrl.INSTANCE.describeTo(description)
        then:
            description.toString() == 'a string which is valid URL'
    }
}
