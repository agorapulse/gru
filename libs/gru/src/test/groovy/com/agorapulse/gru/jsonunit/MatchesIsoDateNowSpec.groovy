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
package com.agorapulse.gru.jsonunit

import org.hamcrest.Description
import org.hamcrest.StringDescription
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant

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
            true    | new DateTime().toString(ISODateTimeFormat.dateHourMinuteSecondFraction()) + 'Z'
            true    | new DateTime().toString(ISODateTimeFormat.dateTimeNoMillis())
            true    | new DateTime().toString(ISODateTimeFormat.dateTimeNoMillis())
            true    | Instant.now().toString()
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
