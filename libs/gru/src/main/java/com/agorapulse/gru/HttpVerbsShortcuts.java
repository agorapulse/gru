/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2023 Agorapulse.
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//CHECKSTYLE:OFF
public interface HttpVerbsShortcuts {
    String HEAD = "HEAD";
    String POST = "POST";
    String PUT = "PUT";
    String PATCH = "PATCH";
    String DELETE = "DELETE";
    String OPTIONS = "OPTIONS";
    String TRACE = "TRACE";
    String GET = "GET";

    List<String> HAS_URI_PARAMETERS = Collections.unmodifiableList(Arrays.asList(
        HEAD, DELETE, OPTIONS, TRACE, GET
    ));


}
