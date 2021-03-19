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
