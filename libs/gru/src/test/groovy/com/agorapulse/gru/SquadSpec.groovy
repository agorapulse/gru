/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2023 Agorapulse.
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

import com.agorapulse.gru.minions.AbstractMinion
import com.agorapulse.gru.minions.HttpMinion
import spock.lang.Specification

/**
 * Tests for Squad.
 */
class SquadSpec extends Specification {

    void 'asking minion which is not in squad returns null'() {
        expect:
            new Squad().ask(HttpMinion) { responseHeaders } == null
    }

    void 'minion must be instantiable'() {
        when:
            new Squad().command(AbstractMinion) { }
        then:
            thrown(IllegalStateException)
    }

}
