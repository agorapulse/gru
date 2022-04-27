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

import com.agorapulse.gru.minions.Command;
import com.agorapulse.gru.minions.Minion;
import net.javacrumbs.jsonunit.core.internal.JsonUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Prepares the request for controller action.
 */
public interface RequestDefinitionBuilder extends WithContentSupport {

    /**
     * @see Squad#command(Class, Command)
     */
    <M extends Minion> RequestDefinitionBuilder command(Class<M> minionType, Command<M> command);



    /**
     * Sets a JSON request from given file.
     * The file must reside in same package as the test in the directory with the same name as the test
     * e.g. src/test/resources/org/example/foo/MySpec.
     * The file is created automatically during first run if it does not exist yet with empty object definition.
     *
     * It automatically applies Content-Type: application/json header.
     *
     * @param relativePath a JSON request file
     * @return self
     */
    RequestDefinitionBuilder json(String relativePath);

    /**
     * Sets a JSON request from given content.
     *
     * It automatically applies Content-Type: application/json header.
     *
     * @param content a JSON request file
     * @return self
     */
    RequestDefinitionBuilder json(Content content);

    /**
     * Sets a JSON request from given content.
     *
     * It automatically applies Content-Type: application/json header.
     *
     * @param map map to be converted to JSON
     * @return self
     */
    default RequestDefinitionBuilder json(Map<?, ?> map) {
        return json(inline(JsonUtils.convertToJson(map, "request").toString()));
    }

    /**
     * Sets a JSON request from given content.
     *
     * It automatically applies Content-Type: application/json header.
     *
     * @param list list to be converted to JSON
     * @return self
     */
    default RequestDefinitionBuilder json(List<?> list) {
        return json(inline(JsonUtils.convertToJson(list, "request").toString()));
    }

    /**
     * Sets a payload for the request from given file.
     * The file must reside in same package as the test in the directory with the same name as the test
     * e.g. src/test/resources/org/example/foo/MySpec.
     * The file is created automatically during first run if it does not exist yet with empty object definition.
     *
     * It automatically applies Content-Type header.
     *
     * @param relativePath relative path to the payload file for the request
     * @param contentType content type of the payload
     * @return self
     */
    RequestDefinitionBuilder content(String relativePath, String contentType);

    /**
     * Sets a request payload from given content.
     *
     * It automatically applies Content-Type header.
     *
     * @param content payload for the request
     * @param contentType content type of the payload
     * @return self
     */
    RequestDefinitionBuilder content(Content content, String contentType);

    /**
     * Adds URL parameters for the action execution.
     *
     * @param params additional URL parameters for the action execution
     * @return self
     */
    RequestDefinitionBuilder params(Map<String, Object> params);

    default RequestDefinitionBuilder param(String name, String value) {
        return params(Collections.singletonMap(name, value));
    }

    /**
     * Adds HTTP headers for the action execution.
     *
     * @param headers additional HTTP headers for the action execution
     * @return self
     */
    RequestDefinitionBuilder headers(Map<String, String> headers);

    RequestDefinitionBuilder upload(Consumer<MultipartDefinitionBuilder> definition);

    /**
     * Add an HTTP cookie to the action execution.
     * @param name name of the cookie
     * @param value value of the cookie
     * @return self
     */
    default RequestDefinitionBuilder cookie(String name, String value) {
        return cookies(Collections.singletonMap(name, value));
    }


    /**
     * Add an HTTP cookies to the action execution.
     * @param cookies cookies to be added
     * @return self
     */
    RequestDefinitionBuilder cookies(Map<String, String> cookies);
}
