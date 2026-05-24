/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2026 Agorapulse.
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
package com.agorapulse.gru.grails.minions.jsonview;

/**
 * Detects whether the JSON views plugin is on the classpath so the optional
 * {@link JsonViewRendererMinion} can be activated.
 */
public final class JsonViewSupport {

    public static final Class<?> JSON_VIEWS_PLUGIN_TYPE;

    static {
        Class<?> type;
        try {
            type = Class.forName("grails.plugin.json.view.JsonViewGrailsPlugin");
        } catch (ClassNotFoundException ignored) {
            type = null;
        }
        JSON_VIEWS_PLUGIN_TYPE = type;
    }

    private JsonViewSupport() {
    }

    public static boolean isEnabled() {
        return JSON_VIEWS_PLUGIN_TYPE != null;
    }
}
