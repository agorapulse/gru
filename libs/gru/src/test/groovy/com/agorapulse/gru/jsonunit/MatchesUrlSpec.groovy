/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2021 Agorapulse.
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
