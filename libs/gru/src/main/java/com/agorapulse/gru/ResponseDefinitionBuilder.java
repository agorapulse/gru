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

import com.agorapulse.gru.minions.Command;
import com.agorapulse.gru.minions.Minion;
import net.javacrumbs.jsonunit.assertj.JsonAssert;
import net.javacrumbs.jsonunit.core.Option;
import net.javacrumbs.jsonunit.core.internal.JsonUtils;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.intellij.lang.annotations.Language;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Sets expectations for the response after the controller action has been executed.
 *
 */
public interface ResponseDefinitionBuilder extends HttpStatusShortcuts, JsonUnitOptionsShortcuts, WithContentSupport {

    /**
     * @see Squad#command(Class, Command)
     */
    <M extends Minion> ResponseDefinitionBuilder command(Class<M> minionType, Command<M> command);

    /**
     * Sets an expected status returned.
     * Defaults to OK.
     *
     * @param aStatus an expected status returned
     * @return self
     */
    ResponseDefinitionBuilder status(int aStatus);

    /**
     * Sets the acceptable statuses returned.
     * Defaults to OK.
     *
     * @param statuses the acceptable statuses
     * @return self
     */
    ResponseDefinitionBuilder statuses(int... statuses);


    /**
     * Sets an expected JSON response from given file.
     * The file must reside in same package as the test in the directory with the same name as the test
     * e.g. src/test/resources/org/example/foo/MySpec.
     * The file is created automatically during first run if it does not exist yet with the returned JSON.
     *
     * @param relativePath an expected JSON response file
     * @return self
     */
    ResponseDefinitionBuilder json(String relativePath);

    /**
     * Sets an expected JSON response from given content.
     *
     * @param content direct content
     * @return self
     */
    ResponseDefinitionBuilder json(Content content);

    /**
     * Sets a JSON request from given content.
     *
     * It automatically applies Content-Type: application/json header.
     *
     * @param map map to be converted to JSON
     * @return self
     */
    default ResponseDefinitionBuilder json(Map<?, ?> map) {
        return json(inline(JsonUtils.convertToJson(map, "response").toString()));
    }

    /**
     * Sets a JSON request from given content.
     *
     * It automatically applies Content-Type: application/json header.
     *
     * @param list list to be converted to JSON
     * @return self
     */
    default ResponseDefinitionBuilder json(List<?> list) {
        return json(inline(JsonUtils.convertToJson(list, "response").toString()));
    }


    /**
     * Sets an expected HTML response from given file.
     * The file must reside in same package as the test in the directory with the same name as the test
     * e.g. src/test/resources/org/example/foo/MySpec.
     * The file is created automatically during first run if it does not exist yet with the returned HTML.
     *
     * @param relativePath an expected HTML response file
     * @return self
     */
    ResponseDefinitionBuilder html(String relativePath);

    /**
     * Sets an expected HTML response from given content.
     *
     *  @param content an expected HTML response file
     * @return self
     */
    ResponseDefinitionBuilder html(Content content);

    /**
     * Sets an expected text response from given file.
     * The file must reside in same package as the test in the directory with the same name as the test
     * e.g. src/test/resources/org/example/foo/MySpec.
     * The file is created automatically during first run if it does not exist yet with the returned HTML.
     *
     * @param relativePath an expected HTML response file
     * @return self
     */
    ResponseDefinitionBuilder text(String relativePath);
    /**
     * Sets an expected text response from given content.
     *
     * @param content an expected HTML response file
     * @return self
     */
    ResponseDefinitionBuilder text(Content content);

    /**
     * Sets additional assertions and configuration for JsonFluentAssert instance which is testing the response.
     *
     * @param additionalConfiguration additional assertions and configuration for JsonFluentAssert instance
     * @return self
     */
    ResponseDefinitionBuilder json(UnaryOperator<JsonAssert.ConfigurableJsonAssert> additionalConfiguration);

    /**
     * Adds HTTP headers which are expected to be returned after action execution.
     *
     * @param additionalHeaders additional HTTP headers which are expected to be returned after action execution
     * @return self
     */
    default ResponseDefinitionBuilder headers(Map<String, String> additionalHeaders) {
        additionalHeaders
            .forEach((k, v) -> header(k, Matchers.equalTo(v)));

        return this;
    }

    default ResponseDefinitionBuilder header(@Language("http-header-reference") String name, String value) {
        return header(name, Matchers.equalTo(value));
    }

    ResponseDefinitionBuilder header(@Language("http-header-reference") String name, Matcher<String> matcher);

    /**
     * Sets the expected redirection URI.
     *
     * @param uri expected URI
     * @return self
     */
    ResponseDefinitionBuilder redirect(String uri);

    /**
     * Sets an expected JSON response from given file.
     * The file must reside in same package as the test in the directory with the same name as the test
     * e.g. src/test/resources/org/example/foo/MySpec.
     * The file is created automatically during first run if it does not exist yet with the returned JSON.
     *
     * @param relativePath an expected JSON response file
     * @param option       JsonUnit option, e.g. IGNORING_EXTRA_ARRAY_ITEMS
     * @return self other JsonUnit options e.g. IGNORING_EXTRA_ARRAY_ITEMS
     */
    ResponseDefinitionBuilder json(String relativePath, Option option, Option... options);

    /**
     * Expect a cookie returned by the server.
     * @param cookieDefinition definition of the cookie
     * @return self
     */
    ResponseDefinitionBuilder cookie(Consumer<ResponseCookieDefinition> cookieDefinition);

    default ResponseDefinitionBuilder cookies(Map<String, String> cookies) {
        cookies.forEach((name, value) -> cookie(c -> c.name(name).value(value)));
        return this;
    }

    default ResponseDefinitionBuilder cookie(String name, String value) {
        cookie(c -> c.name(name).value(value));
        return this;
    }
}
