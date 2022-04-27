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

import com.agorapulse.gru.content.FileContent;
import com.agorapulse.gru.cookie.Cookie;
import com.agorapulse.gru.minions.Command;
import com.agorapulse.gru.minions.CookieMinion;
import com.agorapulse.gru.minions.HtmlMinion;
import com.agorapulse.gru.minions.HttpMinion;
import com.agorapulse.gru.minions.JsonMinion;
import com.agorapulse.gru.minions.Minion;
import com.agorapulse.gru.minions.TextMinion;
import net.javacrumbs.jsonunit.core.Option;
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Sets expectations for the response after the controller action has been executed.
 *
 */
public class DefaultResponseDefinitionBuilder implements ResponseDefinitionBuilder {

    protected DefaultResponseDefinitionBuilder(Squad squad) {
        this.squad = squad;
    }

    /**
     * @see Squad#command(Class, Command)
     */
    public <M extends Minion> DefaultResponseDefinitionBuilder command(Class<M> minionType, Command<M> command) {
        this.squad.command(minionType, command);
        return this;
    }

    /**
     * Sets a expected status returned.
     * Defaults to OK.
     *
     * @param aStatus a expected status returned
     * @return self
     */
    public DefaultResponseDefinitionBuilder status(final int aStatus) {
        return command(HttpMinion.class, minion -> minion.setStatus(aStatus));
    }

    /**
     * Sets a multiple possible status returned.
     * Defaults to OK.
     *
     * @param statuses a possible statuses to be returned
     * @return self
     */
    public DefaultResponseDefinitionBuilder statuses(final int... statuses) {
        return command(HttpMinion.class, minion -> minion.setStatuses(statuses));
    }

    public DefaultResponseDefinitionBuilder json(final Content content) {
        return command(JsonMinion.class, minion -> minion.setResponseContent(content));
    }

    public DefaultResponseDefinitionBuilder html(final Content content) {
        return command(HtmlMinion.class, minion -> minion.setResponseContent(content));
    }

    @Override
    public DefaultResponseDefinitionBuilder json(String content) {
        return json(FileContent.create(content));
    }

    @Override
    public DefaultResponseDefinitionBuilder html(String content) {
        return html(FileContent.create(content));
    }

    @Override
    public DefaultResponseDefinitionBuilder text(String content) {
        return text(FileContent.create(content));
    }

    @Override
    public DefaultResponseDefinitionBuilder text(final Content content) {
        return command(TextMinion.class, minion -> minion.setResponseContent(content));
    }

    /**
     * Sets additional assertions and configuration for JsonFluentAssert instance which is testing the response.
     *
     * @param additionalConfiguration additional assertions and configuration for JsonFluentAssert instance
     * @return self
     */
    public DefaultResponseDefinitionBuilder json(Function<JsonFluentAssert.ConfigurableJsonFluentAssert, JsonFluentAssert.ConfigurableJsonFluentAssert> additionalConfiguration) {
        return command(JsonMinion.class, minion -> minion.setJsonUnitConfiguration(additionalConfiguration));
    }

    /**
     * Adds HTTP headers which are expected to be returned after action execution.
     *
     * @param additionalHeaders additional HTTP headers which are expected to be returned after action execution
     * @return self
     */
    public DefaultResponseDefinitionBuilder headers(final Map<String, String> additionalHeaders) {
        return command(HttpMinion.class, minion ->
            additionalHeaders.forEach((key, value) ->
                minion.getResponseHeaders().computeIfAbsent(key, k -> new ArrayList<>()).add(value)
            )
        );
    }

    /**
     * Sets the expected redirection URI.
     *
     * @param uri expected URI
     * @return self
     */
    public DefaultResponseDefinitionBuilder redirect(final String uri) {
        return command(HttpMinion.class, minion -> {
            minion.setRedirectUri(uri);
            minion.setStatuses(MOVED_PERMANENTLY, FOUND, TEMPORARY_REDIRECT, PERMANENT_REDIRECT);
        });
    }

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
    public DefaultResponseDefinitionBuilder json(String relativePath, final Option option, final Option... options) {
        json(relativePath);
        return json((a) -> a.when(option, options));
    }

    @Override
    public DefaultResponseDefinitionBuilder cookie(Consumer<ResponseCookieDefinition> cookieDefinition) {
        Cookie.Builder builder = new Cookie.Builder();
        cookieDefinition.accept(builder);
        command(CookieMinion.class, m -> m.getResponseCookies().add(builder.build()));
        return this;
    }

    private Squad squad;
}
