/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2024 Agorapulse.
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

import com.agorapulse.gru.HttpVerbsShortcuts
import com.agorapulse.gru.minions.Minion
import org.intellij.lang.annotations.Language
import com.agorapulse.gru.TestDefinitionBuilder as JavaTestDefinitionBuilder

class TestDefinitionBuilder(private val delegate: JavaTestDefinitionBuilder) : HttpVerbsShortcuts {

    /**
     * @see Squad.command
     */
    fun <M : Minion> command(minionType: Class<M>, command: M.() -> Unit): TestDefinitionBuilder {
        delegate.command(minionType) { command(it) }
        return this
    }

    fun expect(definition: ResponseDefinitionBuilder.() -> ResponseDefinitionBuilder): TestDefinitionBuilder {
        delegate.expect { definition(ResponseDefinitionBuilder(it)) }
        return this
    }

    fun head(
        @Language("HTTP Request") uri: String,
        definition: RequestDefinitionBuilder.() -> RequestDefinitionBuilder
    ): TestDefinitionBuilder {
        delegate.head(uri) { definition(RequestDefinitionBuilder(it)) }
        return this
    }

    fun head(@Language("HTTP Request") uri: String): TestDefinitionBuilder {
        delegate.head(uri)
        return this
    }


    fun post(
        @Language("HTTP Request") uri: String,
        definition: RequestDefinitionBuilder.() -> RequestDefinitionBuilder
    ): TestDefinitionBuilder {
        delegate.post(uri) { definition(RequestDefinitionBuilder(it)) }
        return this
    }

    fun post(@Language("HTTP Request") uri: String): TestDefinitionBuilder {
        delegate.post(uri)
        return this
    }

    fun put(
        @Language("HTTP Request") uri: String,
        definition: RequestDefinitionBuilder.() -> RequestDefinitionBuilder
    ): TestDefinitionBuilder {
        delegate.put(uri) { definition(RequestDefinitionBuilder(it)) }
        return this
    }

    fun put(@Language("HTTP Request") uri: String): TestDefinitionBuilder {
        delegate.put(uri)
        return this
    }

    fun patch(
        @Language("HTTP Request") uri: String,
        definition: RequestDefinitionBuilder.() -> RequestDefinitionBuilder
    ): TestDefinitionBuilder {
        delegate.patch(uri) { definition(RequestDefinitionBuilder(it)) }
        return this
    }

    fun patch(@Language("HTTP Request") uri: String): TestDefinitionBuilder {
        delegate.patch(uri)
        return this
    }

    fun delete(
        @Language("HTTP Request") uri: String,
        definition: RequestDefinitionBuilder.() -> RequestDefinitionBuilder
    ): TestDefinitionBuilder {
        delegate.delete(uri) { definition(RequestDefinitionBuilder(it)) }
        return this
    }

    fun delete(@Language("HTTP Request") uri: String): TestDefinitionBuilder {
        delegate.delete(uri)
        return this

    }

    fun options(
        @Language("HTTP Request") uri: String,
        definition: RequestDefinitionBuilder.() -> RequestDefinitionBuilder
    ): TestDefinitionBuilder {
        delegate.options(uri) { definition(RequestDefinitionBuilder(it)) }
        return this
    }

    fun options(@Language("HTTP Request") uri: String): TestDefinitionBuilder {
        delegate.options(uri)
        return this
    }

    fun trace(
        @Language("HTTP Request") uri: String,
        definition: RequestDefinitionBuilder.() -> RequestDefinitionBuilder
    ): TestDefinitionBuilder {
        delegate.trace(uri) { definition(RequestDefinitionBuilder(it)) }
        return this
    }

    fun trace(@Language("HTTP Request") uri: String): TestDefinitionBuilder {
        delegate.trace(uri)
        return this
    }

    fun get(
        @Language("HTTP Request") uri: String,
        definition: RequestDefinitionBuilder.() -> RequestDefinitionBuilder
    ): TestDefinitionBuilder {
        delegate.get(uri) { definition(RequestDefinitionBuilder(it)) }
        return this
    }

    fun get(@Language("HTTP Request") uri: String): TestDefinitionBuilder {
        delegate.get(uri)
        return this
    }

    fun baseUri(uri: String): TestDefinitionBuilder {
        delegate.baseUri(uri)
        return this
    }

}
