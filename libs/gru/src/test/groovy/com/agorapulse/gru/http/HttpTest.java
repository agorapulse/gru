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
package com.agorapulse.gru.http;

import com.agorapulse.gru.Gru;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HttpTest {

    @Test
    void testCreateWithoutSpecifyingUnitTest() {
        try (Gru gru = Gru.create("http://localhost:8080")) {
            Assertions.assertNotNull(gru);
        }
    }

}
