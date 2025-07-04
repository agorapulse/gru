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
package com.agorapulse.gru;

import com.agorapulse.gru.content.FileContent;
import org.intellij.lang.annotations.Language;

import java.util.Collections;
import java.util.Map;

public interface MultipartDefinitionBuilder extends WithContentSupport {

    MultipartDefinitionBuilder params(Map<String, Object> params);

    default MultipartDefinitionBuilder param(String name, Object value) {
        return params(Collections.singletonMap(name, value));
    }

    MultipartDefinitionBuilder file(String parameterName, String filename, Content content, @Language("mime-type-reference") String contentType);

    default MultipartDefinitionBuilder file(String parameterName, String filename, String relativePath, @Language("mime-type-reference") String contentType) {
        return file(parameterName, filename, FileContent.create(relativePath), contentType);
    }

}
