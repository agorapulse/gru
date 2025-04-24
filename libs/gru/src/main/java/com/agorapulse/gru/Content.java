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
import com.agorapulse.gru.content.StringContent;

import java.io.IOException;
import java.io.InputStream;

/**
 * Opaque content wrapper.
 */
public interface Content {

    static Content inline(String content) {
        return StringContent.create(content);
    }

    static Content file(String content) {
        return FileContent.create(content);
    }

    /**
     * Loads a content.
     * @return the content represented as InputStr¨
     */
    InputStream load(Client client);

    /**
     * @return true if saving is supported for this content implementation.
     */
    boolean isSaveSupported();

    void save(Client client, InputStream stream) throws IOException;

}
