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
import org.intellij.lang.annotations.Language;

import java.util.function.Consumer;

public interface TestDefinitionBuilder extends HttpVerbsShortcuts {

    /**
     * @see Squad#command(Class, Command)
     */
    <M extends Minion> TestDefinitionBuilder command(Class<M> minionType, Command<M> command);

    TestDefinitionBuilder expect(Consumer<ResponseDefinitionBuilder> definition);

    TestDefinitionBuilder head(@Language("http-url-reference") String uri, Consumer<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder head(@Language("http-url-reference") String uri);

    TestDefinitionBuilder post(@Language("http-url-reference") String uri, Consumer<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder post(@Language("http-url-reference") String uri);

    TestDefinitionBuilder put(@Language("http-url-reference") String uri, Consumer<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder put(@Language("http-url-reference") String uri);

    TestDefinitionBuilder patch(@Language("http-url-reference") String uri, Consumer<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder patch(@Language("http-url-reference") String uri);

    TestDefinitionBuilder delete(@Language("http-url-reference") String uri, Consumer<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder delete(@Language("http-url-reference") String uri);

    TestDefinitionBuilder options(@Language("http-url-reference") String uri, Consumer<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder options(@Language("http-url-reference") String uri);

    TestDefinitionBuilder trace(@Language("http-url-reference") String uri, Consumer<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder trace(@Language("http-url-reference") String uri);

    TestDefinitionBuilder get(@Language("http-url-reference") String uri, Consumer<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder get(@Language("http-url-reference") String uri);

    TestDefinitionBuilder baseUri(String uri);

}
