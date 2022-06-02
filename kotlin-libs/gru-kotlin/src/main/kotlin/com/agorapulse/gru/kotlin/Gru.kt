/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2022 Agorapulse.
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
package com.agorapulse.gru.kotlin

import com.agorapulse.gru.*
import com.agorapulse.gru.minions.Minion
import java.io.Closeable
import com.agorapulse.gru.Gru as JavaGru

fun create(client: Client): Gru {
    return Gru(JavaGru.create(client))
}

/**
 * Gru steals the controller's unit test to verify controller's actions in context.
 *
 *
 */
class Gru constructor(private val delegate: JavaGru) : Closeable {

    /**
     * Prepare every test with following configuration.
     *
     * @param configuration configuration applied to every feature method
     * @return self
     */
    fun prepare(
        configuration: TestDefinitionBuilder.() -> TestDefinitionBuilder
    ): Gru {
        delegate.prepare { configuration(TestDefinitionBuilder(it)) }
        return this
    }

    /**
     * Prepare tests with given base URI.
     *
     * @param baseUri the base URI for the test calls
     * @return self
     */
    fun prepare(baseUri: String): Gru {
        delegate.prepare(baseUri)
        return this
    }

    /**
     * Adds minion to the squad for current test.
     *
     * @param minion minion to be added to the squad
     * @return self
     */
    fun engage(minion: Minion): Gru {
        delegate.engage(minion)
        return this
    }

    /**
     * Checks if the expectations has been verified and resets the internal state.
     */
    override fun close() {
        delegate.close()
    }

    /**
     * Defines and verifies API test and runs the controller initialization and the action under test.
     *
     *
     *
     * @param expectation test definition
     */
    @Throws(Throwable::class)
    fun verify(expectation: TestDefinitionBuilder.() -> TestDefinitionBuilder) {
        delegate.verify { expectation(TestDefinitionBuilder(it)) }
    }

    /**
     * Defines API test and runs the controller initialization and the action under test.
     *
     *
     * Use this method either in when or expect block.
     *
     * @param expectation test definition
     * @return self, note that when Groovy Truth is evaluated, `verify` method is called automatically
     */
    fun test(expectation: TestDefinitionBuilder.() -> TestDefinitionBuilder): Gru {
        delegate.test { expectation(TestDefinitionBuilder(it)) }
        return this
    }

    /**
     * Verifies all expectations.
     *
     * @return true if all verifications are successful
     * @throws AssertionError if any verification fails
     */
    @Throws(Throwable::class)
    fun verify(): Boolean {
        return delegate.verify()
    }


    /**
     * Reset the internal state. This is done by the rule automatically.
     *
     * @param resetConfigurations also clear the configurations created using [.prepare] method
     */
    /**
     * Reset the internal state. This is done by the rule automatically.
     */
    @JvmOverloads
    fun reset(resetConfigurations: Boolean = true): Gru {
        delegate.reset(resetConfigurations)
        return this
    }

}
