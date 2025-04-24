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
import com.agorapulse.gru.content.FileContent
import org.intellij.lang.annotations.Language
import java.util.*
import com.agorapulse.gru.MultipartDefinitionBuilder as JavaMultipartDefinitionBuilder

class MultipartDefinitionBuilder(private val delegate: JavaMultipartDefinitionBuilder) : WithContentSupport {

    fun params(params: Map<String, Any>): MultipartDefinitionBuilder {
        delegate.params(params)
        return this
    }

    fun param(name: String, value: Any): MultipartDefinitionBuilder {
        return params(Collections.singletonMap(name, value))
    }

    fun file(
        parameterName: String,
        filename: String,
        content: Content,
        @Language("mime-type-reference") contentType: String
    ): MultipartDefinitionBuilder {
        delegate.file(parameterName, filename, content, contentType)
        return this
    }

    fun file(
        parameterName: String,
        filename: String,
        relativePath: String,
        @Language("mime-type-reference") contentType: String
    ): MultipartDefinitionBuilder {
        return file(parameterName, filename, FileContent.create(relativePath), contentType)
    }

}
