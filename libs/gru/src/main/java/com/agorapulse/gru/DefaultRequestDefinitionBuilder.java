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
import com.agorapulse.gru.cookie.Cookie;
import com.agorapulse.gru.minions.Command;
import com.agorapulse.gru.minions.CookieMinion;
import com.agorapulse.gru.minions.HttpMinion;
import com.agorapulse.gru.minions.JsonMinion;
import com.agorapulse.gru.minions.Minion;
import com.agorapulse.gru.minions.MultipartMinion;
import com.agorapulse.gru.minions.ParametersMinion;
import com.agorapulse.gru.minions.PayloadMinion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Prepares the request for controller action.
 *
 */
public final class DefaultRequestDefinitionBuilder implements RequestDefinitionBuilder {

    protected DefaultRequestDefinitionBuilder(Squad squad) {
        this.squad = squad;
    }

    /**
     * @see Squad#command(Class, Command)
     */
    public <M extends Minion> DefaultRequestDefinitionBuilder command(Class<M> minionType, Command<M> command) {
        this.squad.command(minionType, command);
        return this;
    }

    public DefaultRequestDefinitionBuilder json(final Content content) {
        headers(Collections.singletonMap("Content-Type", "application/json"));
        return command(JsonMinion.class, minion -> minion.setRequestJsonContent(content));
    }

    @Override
    public DefaultRequestDefinitionBuilder json(String relativePath) {
        return json(FileContent.create(relativePath));
    }

    @Override
    public DefaultRequestDefinitionBuilder content(String relativePath, String contentType) {
        return content(FileContent.create(relativePath), contentType);
    }

    @Override
    public DefaultRequestDefinitionBuilder content(Content content, String contentType) {
        return command(PayloadMinion.class, minion -> {
            minion.setPayload(content);
            minion.setContentType(contentType);
        });
    }

    /**
     * Adds URL parameters for the action execution.
     *
     * @param params additional URL parameters for the action execution
     * @return self
     */
    public DefaultRequestDefinitionBuilder params(final Map<String, Object> params) {
        return command(ParametersMinion.class, minion -> minion.addParameters(params));
    }

    /**
     * Adds HTTP headers for the action execution.
     *
     * @param headers additional HTTP headers for the action execution
     * @return self
     */
    public DefaultRequestDefinitionBuilder headers(final Map<String, String> headers) {
        return command(HttpMinion.class, minion ->
            headers.forEach((key, value) ->
                minion.getRequestHeaders().computeIfAbsent(key, k -> new ArrayList<>()).add(value)
            )
        );
    }

    @Override
    public DefaultRequestDefinitionBuilder upload(Consumer<MultipartDefinitionBuilder> definition) {
        command(MultipartMinion.class, minion -> minion.multipart(definition));
        return this;
    }

    @Override
    public DefaultRequestDefinitionBuilder cookies(Map<String, String> cookies) {
        command(CookieMinion.class, m -> cookies.forEach((name, value) ->
            m.getRequestCookies().add(new Cookie.Builder().name(name).value(value).build()))
        );
        return this;
    }

    private Squad squad;
}
