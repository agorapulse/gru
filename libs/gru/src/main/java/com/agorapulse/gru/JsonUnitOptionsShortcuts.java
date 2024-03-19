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
package com.agorapulse.gru;

import net.javacrumbs.jsonunit.core.Option;

/**
 * Provides JsonUnit options' shortcuts for <code>expect</code> block so they can be used without additional imports.
 */
//CHECKSTYLE:OFF
public interface JsonUnitOptionsShortcuts {

    Option TREATING_NULL_AS_ABSENT = Option.TREATING_NULL_AS_ABSENT;
    Option IGNORING_ARRAY_ORDER = Option.IGNORING_ARRAY_ORDER;
    Option IGNORING_EXTRA_FIELDS = Option.IGNORING_EXTRA_FIELDS;
    Option IGNORING_EXTRA_ARRAY_ITEMS = Option.IGNORING_EXTRA_ARRAY_ITEMS;
    @Deprecated Option COMPARING_ONLY_STRUCTURE = Option.COMPARING_ONLY_STRUCTURE;
    Option IGNORING_VALUES = Option.IGNORING_VALUES;

}
