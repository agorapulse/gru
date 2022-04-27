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
package com.agorapulse.gru;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultMultipartDefinitionBuilder implements MultipartDefinitionBuilder, MultipartDefinition {

    private final Client client;
    private final Map<String, Object> parameters = new LinkedHashMap<>();
    private final Map<String, MultipartFile> files = new LinkedHashMap<>();

    public DefaultMultipartDefinitionBuilder(Client client) {
        this.client = client;
    }

    @Override
    public MultipartDefinitionBuilder params(Map<String, Object> params) {
        parameters.putAll(params);
        return this;
    }

    @Override
    public MultipartDefinitionBuilder file(String parameterName, String filename, Content content, String contentType) {
        files.put(parameterName, new DefaultMultipartFile(client, parameterName, filename, content, contentType));
        return this;
    }

    public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    public Map<String, MultipartFile> getFiles() {
        return Collections.unmodifiableMap(files);
    }

}
