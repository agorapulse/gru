package com.agorapulse.gru.jsonunit

import org.hamcrest.Description
import org.hamcrest.StringDescription
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Tests for MatchesIsoDateNow.
 */
@Unroll
class MatchesIsoDateNowSpec extends Specification {

    void 'string #date #matchesDesc'() {
        expect:
            MatchesIsoDateNow.INSTANCE.matches(date) == matches

        where:
            matches | date
            true    | new DateTime().toString(ISODateTimeFormat.dateTime())
            true    | new DateTime().toString(ISODateTimeFormat.dateTimeNoMillis())
            false   | 'foo.bar'
            false   | null

            matchesDesc = matches ? 'matches' : 'does not match'

    }

    void 'test description'() {
        given:
            Description description = new StringDescription()
        when:
            MatchesIsoDateNow.INSTANCE.describeTo(description)
        then:
            description.toString() == 'a string matching ISO date time which is not older than one hour'
    }

}
