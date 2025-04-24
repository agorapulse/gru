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

import com.agorapulse.gru.*
import com.agorapulse.gru.minions.Minion
import net.javacrumbs.jsonunit.assertj.JsonAssert.ConfigurableJsonAssert
import net.javacrumbs.jsonunit.core.Option
import net.javacrumbs.jsonunit.core.internal.JsonUtils
import org.intellij.lang.annotations.Language
import java.util.*
import com.agorapulse.gru.ResponseDefinitionBuilder as JavaResponseDefinitionBuilder

/**
 * Sets expectations for the response after the controller action has been executed.
 *
 */
class ResponseDefinitionBuilder(val delegate: JavaResponseDefinitionBuilder) : HttpStatusShortcuts, JsonUnitOptionsShortcuts, WithContentSupport {

    /**
     * @see Squad.command
     */
    inline fun <reified M : Minion> command(noinline command: M.() -> Unit): ResponseDefinitionBuilder {
        delegate.command(M::class.java) { command(it) }
        return this
    }

    /**
     * Sets an expected status returned.
     * Defaults to OK.
     *
     * @param aStatus an expected status returned
     * @return self
     */
    fun status(aStatus: Int): ResponseDefinitionBuilder {
        delegate.status(aStatus)
        return this
    }

    /**
     * Sets the acceptable statuses returned.
     * Defaults to OK.
     *
     * @param statuses the acceptable statuses
     * @return self
     */
    fun statuses(vararg statuses: Int): ResponseDefinitionBuilder {
        delegate.statuses(*statuses)
        return this
    }

    /**
     * Sets an expected JSON response from given file.
     * The file must reside in same package as the test in the directory with the same name as the test
     * e.g. src/test/resources/org/example/foo/MySpec.
     * The file is created automatically during first run if it does not exist yet with the returned JSON.
     *
     * @param relativePath an expected JSON response file
     * @return self
     */
    fun json(relativePath: String): ResponseDefinitionBuilder {
        delegate.json(relativePath)
        return this
    }

    /**
     * Sets an expected JSON response from given content.
     *
     * @param content direct content
     * @return self
     */
    fun json(content: Content): ResponseDefinitionBuilder {
        delegate.json(content)
        return this
    }

    /**
     * Sets a JSON request from given content.
     *
     * It automatically applies Content-Type: application/json header.
     *
     * @param map map to be converted to JSON
     * @return self
     */
    fun json(map: Map<*, *>): ResponseDefinitionBuilder {
        return json(inline(JsonUtils.convertToJson(map, "response").toString()))
    }

    /**
     * Sets a JSON request from given content.
     *
     * It automatically applies Content-Type: application/json header.
     *
     * @param list list to be converted to JSON
     * @return self
     */
    fun json(list: List<*>): ResponseDefinitionBuilder {
        return json(inline(JsonUtils.convertToJson(list, "response").toString()))
    }

    /**
     * Sets an expected HTML response from given file.
     * The file must reside in same package as the test in the directory with the same name as the test
     * e.g. src/test/resources/org/example/foo/MySpec.
     * The file is created automatically during first run if it does not exist yet with the returned HTML.
     *
     * @param relativePath an expected HTML response file
     * @return self
     */
    fun html(relativePath: String): ResponseDefinitionBuilder {
        delegate.html(relativePath)
        return this
    }

    /**
     * Sets an expected HTML response from given content.
     *
     * @param content an expected HTML response file
     * @return self
     */
    fun html(content: Content): ResponseDefinitionBuilder {
        delegate.html(content)
        return this
    }

    /**
     * Sets an expected text response from given file.
     * The file must reside in same package as the test in the directory with the same name as the test
     * e.g. src/test/resources/org/example/foo/MySpec.
     * The file is created automatically during first run if it does not exist yet with the returned HTML.
     *
     * @param relativePath an expected HTML response file
     * @return self
     */
    fun text(relativePath: String): ResponseDefinitionBuilder {
        delegate.text(relativePath)
        return this
    }

    /**
     * Sets an expected text response from given content.
     *
     * @param content an expected HTML response file
     * @return self
     */
    fun text(content: Content): ResponseDefinitionBuilder {
        delegate.text(content)
        return this
    }

    /**
     * Sets additional assertions and configuration for JsonFluentAssert instance which is testing the response.
     *
     * @param additionalConfiguration additional assertions and configuration for JsonFluentAssert instance
     * @return self
     */
    fun json(additionalConfiguration: ConfigurableJsonAssert.() -> ConfigurableJsonAssert): ResponseDefinitionBuilder {
        delegate.json { additionalConfiguration(it) }
        return this
    }

    /**
     * Adds HTTP headers which are expected to be returned after action execution.
     *
     * @param additionalHeaders additional HTTP headers which are expected to be returned after action execution
     * @return self
     */
    fun headers(additionalHeaders: Map<String, String>): ResponseDefinitionBuilder {
        delegate.headers(additionalHeaders)
        return this
    }

    fun header(@Language("http-header-reference") name: String, value: String): ResponseDefinitionBuilder {
        return headers(Collections.singletonMap(name, value))
    }

    /**
     * Sets the expected redirection URI.
     *
     * @param uri expected URI
     * @return self
     */
    fun redirect(uri: String): ResponseDefinitionBuilder {
        delegate.redirect(uri)
        return this
    }

    /**
     * Sets an expected JSON response from given file.
     * The file must reside in same package as the test in the directory with the same name as the test
     * e.g. src/test/resources/org/example/foo/MySpec.
     * The file is created automatically during first run if it does not exist yet with the returned JSON.
     *
     * @param relativePath an expected JSON response file
     * @param option       JsonUnit option, e.g. IGNORING_EXTRA_ARRAY_ITEMS
     * @return self other JsonUnit options e.g. IGNORING_EXTRA_ARRAY_ITEMS
     */
    fun json(relativePath: String, option: Option, vararg options: Option): ResponseDefinitionBuilder {
        delegate.json(relativePath, option, *options)
        return this
    }

    /**
     * Expect a cookie returned by the server.
     * @param cookieDefinition definition of the cookie
     * @return self
     */
    fun cookie(cookieDefinition: ResponseCookieDefinition.() -> ResponseCookieDefinition): ResponseDefinitionBuilder {
        delegate.cookie { cookieDefinition(it) }
        return this
    }

    fun cookies(cookies: Map<String, String>): ResponseDefinitionBuilder {
        cookies.forEach { (name: String, value: String) ->
            cookie {
                name(name).value(value)
            }
        }
        return this
    }

    fun cookie(name: String, value: String): ResponseDefinitionBuilder {
        cookie { name(name).value(value) }
        return this
    }
}
