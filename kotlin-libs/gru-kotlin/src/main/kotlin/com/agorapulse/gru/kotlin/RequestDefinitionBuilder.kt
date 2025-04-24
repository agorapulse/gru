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
package com.agorapulse.gru.kotlin

import com.agorapulse.gru.Content
import com.agorapulse.gru.WithContentSupport
import com.agorapulse.gru.minions.Minion
import net.javacrumbs.jsonunit.core.internal.JsonUtils
import org.intellij.lang.annotations.Language
import java.util.*
import com.agorapulse.gru.RequestDefinitionBuilder as JavaRequestDefinitionBuilder

/**
 * Prepares the request for controller action.
 */
class RequestDefinitionBuilder(val delegate: JavaRequestDefinitionBuilder) : WithContentSupport {

    /**
     * @see Squad.command
     */
    inline fun <reified M : Minion> command(noinline command: M.() -> Unit): RequestDefinitionBuilder {
        delegate.command(M::class.java) { command(it) }
        return this
    }


    /**
     * Sets a JSON request from given file.
     * The file must reside in same package as the test in the directory with the same name as the test
     * e.g. src/test/resources/org/example/foo/MySpec.
     * The file is created automatically during first run if it does not exist yet with empty object definition.
     *
     * It automatically applies Content-Type: application/json header.
     *
     * @param relativePath a JSON request file
     * @return self
     */
    fun json(relativePath: String): RequestDefinitionBuilder {
        delegate.json(relativePath)
        return this
    }


    /**
     * Sets a JSON request from given content.
     *
     * It automatically applies Content-Type: application/json header.
     *
     * @param content a JSON request file
     * @return self
     */
    fun json(content: Content): RequestDefinitionBuilder {
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
    fun json(map: Map<String, *>): RequestDefinitionBuilder {
        return json(inline(JsonUtils.convertToJson(map, "request").toString()))
    }

    /**
     * Sets a JSON request from given content.
     *
     * It automatically applies Content-Type: application/json header.
     *
     * @param list list to be converted to JSON
     * @return self
     */
    fun json(list: List<*>): RequestDefinitionBuilder {
        return json(inline(JsonUtils.convertToJson(list, "request").toString()))
    }

    /**
     * Sets a payload for the request from given file.
     * The file must reside in same package as the test in the directory with the same name as the test
     * e.g. src/test/resources/org/example/foo/MySpec.
     * The file is created automatically during first run if it does not exist yet with empty object definition.
     *
     * It automatically applies Content-Type header.
     *
     * @param relativePath relative path to the payload file for the request
     * @param contentType content type of the payload
     * @return self
     */
    fun content(relativePath: String, @Language("mime-type-reference") contentType: String): RequestDefinitionBuilder {
        delegate.content(relativePath, contentType)
        return this
    }


    /**
     * Sets a request payload from given content.
     *
     * It automatically applies Content-Type header.
     *
     * @param content payload for the request
     * @param contentType content type of the payload
     * @return self
     */
    fun content(content: Content, @Language("mime-type-reference") contentType: String): RequestDefinitionBuilder {
        delegate.content(content, contentType)
        return this
    }


    /**
     * Adds URL parameters for the action execution.
     *
     * @param params additional URL parameters for the action execution
     * @return self
     */
    fun params(params: Map<String, Any>): RequestDefinitionBuilder {
        delegate.params(params)
        return this
    }

    fun param(name: String, value: String): RequestDefinitionBuilder {
        return params(Collections.singletonMap<String, Any>(name, value))
    }

    /**
     * Adds HTTP headers for the action execution.
     *
     * @param headers additional HTTP headers for the action execution
     * @return self
     */
    fun headers(headers: Map<String, String>): RequestDefinitionBuilder {
        delegate.headers(headers)
        return this
    }

    fun upload(definition: MultipartDefinitionBuilder.() -> MultipartDefinitionBuilder): RequestDefinitionBuilder {
        delegate.upload { definition(MultipartDefinitionBuilder(it)) }
        return this
    }


    /**
     * Add an HTTP cookie to the action execution.
     * @param name name of the cookie
     * @param value value of the cookie
     * @return self
     */
    fun cookie(name: String, value: String): RequestDefinitionBuilder {
        return cookies(Collections.singletonMap(name, value))
    }

    /**
     * Add an HTTP cookies to the action execution.
     * @param cookies cookies to be added
     * @return self
     */
    fun cookies(cookies: Map<String, String>): RequestDefinitionBuilder {
        delegate.cookies(cookies)
        return this
    }

    fun header(@Language("http-header-reference") name: String, value: String): RequestDefinitionBuilder {
        return headers(Collections.singletonMap(name, value))
    }
}
