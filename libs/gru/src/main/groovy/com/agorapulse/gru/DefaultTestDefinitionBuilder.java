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

import java.util.function.Consumer;

public class DefaultTestDefinitionBuilder implements TestDefinitionBuilder {

    DefaultTestDefinitionBuilder(Client client, Squad squad) {
        this.client = client;
        this.squad = squad;
        this.requestDefinitionBuilder = new DefaultRequestDefinitionBuilder(squad);
        this.responseDefinitionBuilder = new DefaultResponseDefinitionBuilder(squad);
    }

    /**
     * @see com.agorapulse.gru.Squad#command(Class, Command)
     */
    public <M extends Minion> TestDefinitionBuilder command(Class<M> minionType, Command<M> command) {
        this.squad.command(minionType, command);
        return this;
    }

    /**
     * Define expectations about the response after controller action being executed.
     *
     * @param definition expectations about the response after controller action being executed
     * @return self
     */
    @Override
    public TestDefinitionBuilder expect(Consumer<ResponseDefinitionBuilder> definition) {
        definition.accept(responseDefinitionBuilder);
        return this;
    }

    /**
     * Mocks head request and verifies controller action is mapped to given URI.
     *
     * @param uri        URI to which should be the controller action mapped
     * @param definition additional request customisation
     * @return self
     */
    @Override
    public TestDefinitionBuilder head(CharSequence uri, Consumer<RequestDefinitionBuilder> definition) {
        return request(uri, HEAD, definition);
    }

    /**
     * Mocks head request and verifies controller action is mapped to given URI.
     *
     * @param uri URI to which should be the controller action mapped
     * @return self
     */
    @Override
    public TestDefinitionBuilder head(CharSequence uri) {
        return request(uri, HEAD);
    }

    /**
     * Mocks post request and verifies controller action is mapped to given URI.
     *
     * @param uri        URI to which should be the controller action mapped
     * @param definition additional request customisation
     * @return self
     */
    @Override
    public TestDefinitionBuilder post(CharSequence uri, Consumer<RequestDefinitionBuilder> definition) {
        return request(uri, POST, definition);
    }

    /**
     * Mocks post request and verifies controller action is mapped to given URI.
     *
     * @param uri URI to which should be the controller action mapped
     * @return self
     */
    @Override
    public TestDefinitionBuilder post(CharSequence uri) {
        return request(uri, POST);
    }

    /**
     * Mocks put request and verifies controller action is mapped to given URI.
     *
     * @param uri        URI to which should be the controller action mapped
     * @param definition additional request customisation
     * @return self
     */
    @Override
    public TestDefinitionBuilder put(CharSequence uri, Consumer<RequestDefinitionBuilder> definition) {
        return request(uri, PUT, definition);
    }

    /**
     * Mocks put request and verifies controller action is mapped to given URI.
     *
     * @param uri URI to which should be the controller action mapped
     * @return self
     */
    @Override
    public TestDefinitionBuilder put(CharSequence uri) {
        return request(uri, PUT);
    }

    /**
     * Mocks patch request and verifies controller action is mapped to given URI.
     *
     * @param uri        URI to which should be the controller action mapped
     * @param definition additional request customisation
     * @return self
     */
    @Override
    public TestDefinitionBuilder patch(CharSequence uri, Consumer<RequestDefinitionBuilder> definition) {
        return request(uri, PATCH, definition);
    }

    /**
     * Mocks patch request and verifies controller action is mapped to given URI.
     *
     * @param uri URI to which should be the controller action mapped
     * @return self
     */
    @Override
    public TestDefinitionBuilder patch(CharSequence uri) {
        return request(uri, PATCH);
    }

    /**
     * Mocks delete request and verifies controller action is mapped to given URI.
     *
     * @param uri        URI to which should be the controller action mapped
     * @param definition additional request customisation
     * @return self
     */
    @Override
    public TestDefinitionBuilder delete(CharSequence uri, Consumer<RequestDefinitionBuilder> definition) {
        return request(uri, DELETE, definition);
    }

    /**
     * Mocks delete request and verifies controller action is mapped to given URI.
     *
     * @param uri URI to which should be the controller action mapped
     * @return self
     */
    @Override
    public TestDefinitionBuilder delete(CharSequence uri) {
        return request(uri, DELETE);
    }

    /**
     * Mocks options request and verifies controller action is mapped to given URI.
     *
     * @param uri        URI to which should be the controller action mapped
     * @param definition additional request customisation
     * @return self
     */
    @Override
    public TestDefinitionBuilder options(CharSequence uri, Consumer<RequestDefinitionBuilder> definition) {
        return request(uri, OPTIONS, definition);
    }

    /**
     * Mocks options request and verifies controller action is mapped to given URI.
     *
     * @param uri URI to which should be the controller action mapped
     * @return self
     */
    @Override
    public TestDefinitionBuilder options(CharSequence uri) {
        return request(uri, OPTIONS);
    }

    /**
     * Mocks trace request and verifies controller action is mapped to given URI.
     *
     * @param uri        URI to which should be the controller action mapped
     * @param definition additional request customisation
     * @return self
     */
    @Override
    public TestDefinitionBuilder trace(CharSequence uri, Consumer<RequestDefinitionBuilder> definition) {
        return request(uri, TRACE, definition);
    }

    /**
     * Mocks trace request and verifies controller action is mapped to given URI.
     *
     * @param uri URI to which should be the controller action mapped
     * @return self
     */
    @Override
    public TestDefinitionBuilder trace(CharSequence uri) {
        return request(uri, TRACE);
    }

    /**
     * Mocks get request and verifies controller action is mapped to given URI.
     *
     * @param uri        URI to which should be the controller action mapped
     * @param definition additional request customisation
     * @return self
     */
    @Override
    public TestDefinitionBuilder get(CharSequence uri, Consumer<RequestDefinitionBuilder> definition) {
        return request(uri, GET, definition);
    }

    /**
     * Mocks get request and verifies controller action is mapped to given URI.
     *
     * @param uri URI to which should be the controller action mapped
     * @return self
     */
    @Override
    public TestDefinitionBuilder get(CharSequence uri) {
        return request(uri, GET);
    }

    @Override
    public TestDefinitionBuilder baseUri(String uri) {
        client.getRequest().setBaseUri(uri);
        return this;
    }

    private TestDefinitionBuilder request(CharSequence aUrl, String aMethod, Consumer<RequestDefinitionBuilder> definition) {
        client.getRequest().setUri(aUrl.toString());
        client.getRequest().setMethod(aMethod);
        definition.accept(requestDefinitionBuilder);
        return this;
    }

    @SuppressWarnings("unchecked")
    private TestDefinitionBuilder request(CharSequence uri, String method) {
        return request(uri, method, (r) -> {});
    }

    private final Client client;
    private final Squad squad;
    private final RequestDefinitionBuilder requestDefinitionBuilder;
    private final ResponseDefinitionBuilder responseDefinitionBuilder;
}
