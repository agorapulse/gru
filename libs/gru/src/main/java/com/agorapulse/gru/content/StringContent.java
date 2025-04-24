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
package com.agorapulse.gru.content;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.Content;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class StringContent implements Content {

    public static Content create(String content) {
        return new StringContent(content);
    }

    private final String content;

    private StringContent(String content) {
        this.content = content;
    }

    @Override
    public InputStream load(Client client) {
        return new ByteArrayInputStream(content.getBytes());
    }

    @Override
    public boolean isSaveSupported() {
        return false;
    }

    @Override
    public void save(Client client, InputStream stream) {
        throw new UnsupportedOperationException("Saving is not supported for StringContent");
    }

    @Override
    public String toString() {
        return "string content: " + content;
    }
}
