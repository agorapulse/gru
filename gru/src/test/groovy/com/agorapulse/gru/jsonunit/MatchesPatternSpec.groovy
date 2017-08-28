package com.agorapulse.gru.jsonunit

import org.hamcrest.Description
import org.hamcrest.StringDescription
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import spock.lang.Specification

/**
 * Tests for MatchesPattern.
 */
class MatchesPatternSpec extends Specification {

    void 'string #date #matchesDesc'() {
        expect:
            MatchesPattern.ISO_DATE.matches(date) == matches

        where:
            matches | date
            true    | new DateTime().toString(ISODateTimeFormat.dateTime())
            true    | new DateTime().toString(ISODateTimeFormat.dateTimeNoMillis())
            false   | 'foo.bar'
            false   | null

            matchesDesc = matches ? 'matches' : 'does not match'
    }

    void 'string #number #matchesDesc'() {
        expect:
            MatchesPattern.POSITIVE_NUMBER_STRING.matches(number) == matches

        where:
            matches | number
            true    | '123'
            false   | '-123'
            false   | 'foo.bar'
            false   | null

            matchesDesc = matches ? 'matches' : 'does not match'
    }

    void 'test description'() {
        given:
            Description description = new StringDescription()
        when:
            MatchesPattern.POSITIVE_NUMBER_STRING.describeTo(description)
        then:
            description.toString() == "a string matching the pattern '\\d+'"
    }

}
