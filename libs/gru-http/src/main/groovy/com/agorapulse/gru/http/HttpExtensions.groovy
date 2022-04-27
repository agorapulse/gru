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
package com.agorapulse.gru.http

import com.agorapulse.gru.Client
import com.agorapulse.gru.Gru
import groovy.transform.CompileStatic

/**
 * Extensions for Gru and HTTP client.
 */
@CompileStatic
class HttpExtensions {

    /**
     * Prepare gru with given base uri
     * @param gru self
     * @param aBaseUri a base uri
     * @return self with base uri configured
     */
    static <T extends Client> Gru<T> prepare(final Gru<T> gru, final String aBaseUri) {
        gru.prepare {
            baseUri aBaseUri
        }
    }

}
