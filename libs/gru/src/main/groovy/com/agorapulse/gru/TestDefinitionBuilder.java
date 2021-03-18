/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2021 Agorapulse.
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
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FromString;
import groovy.transform.stc.SimpleType;
import space.jasan.support.groovy.closure.ConsumerWithDelegate;

import java.util.function.Consumer;

public interface TestDefinitionBuilder extends HttpVerbsShortcuts {
    /**
     * Shortcut for default url mappings name.
     */
    //CHECKSTYLE:OFF
    String UrlMappings = "UrlMappings";
    //CHECKSTYLE:ON

    /**
     * @see Squad#command(Class, Closure)
     */
    default <M extends Minion> TestDefinitionBuilder command(
        @DelegatesTo.Target Class<M> minionType,
        @DelegatesTo(genericTypeIndex = 0, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = FromString.class, options = "M")
            Closure command
    ) {
        return command(minionType, Command.create(command));
    }

    /**
     * @see Squad#command(Class, Closure)
     */
    <M extends Minion> TestDefinitionBuilder command(Class<M> minionType, Command<M> command);


    default TestDefinitionBuilder expect(
        @DelegatesTo(value = ResponseDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.ResponseDefinitionBuilder")
            Closure<ResponseDefinitionBuilder> definition
    ) {
        return expect(ConsumerWithDelegate.create(definition));
    }

    TestDefinitionBuilder expect(Consumer<ResponseDefinitionBuilder> definition);

    default TestDefinitionBuilder head(
        CharSequence uri,
        @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.ResponseDefinitionBuilder")
            Closure<RequestDefinitionBuilder> definition
    ) {
        return head(uri, ConsumerWithDelegate.create(definition));
    }

    TestDefinitionBuilder head(CharSequence uri, Consumer<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder head(CharSequence uri);

    default TestDefinitionBuilder post(
        CharSequence uri,
        @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.ResponseDefinitionBuilder")
            Closure<RequestDefinitionBuilder> definition
    ) {
        return post(uri, ConsumerWithDelegate.create(definition));
    }

    TestDefinitionBuilder post(CharSequence uri, Consumer<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder post(CharSequence uri);

    default TestDefinitionBuilder put(
        CharSequence uri,
        @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.ResponseDefinitionBuilder")
            Closure<RequestDefinitionBuilder> definition
    ) {
        return put(uri, ConsumerWithDelegate.create(definition));
    }

    TestDefinitionBuilder put(CharSequence uri, Consumer<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder put(CharSequence uri);

    default TestDefinitionBuilder patch(
        CharSequence uri,
        @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.ResponseDefinitionBuilder")
            Closure<RequestDefinitionBuilder> definition
    ) {
        return patch(uri, ConsumerWithDelegate.create(definition));
    }

    TestDefinitionBuilder patch(CharSequence uri, Consumer<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder patch(CharSequence uri);

    default TestDefinitionBuilder delete(
        CharSequence uri,
        @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.ResponseDefinitionBuilder")
            Closure<RequestDefinitionBuilder> definition
    ) {
        return delete(uri, ConsumerWithDelegate.create(definition));
    }

    TestDefinitionBuilder delete(CharSequence uri, Consumer<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder delete(CharSequence uri);

    default TestDefinitionBuilder options(
        CharSequence uri,
        @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.ResponseDefinitionBuilder")
            Closure<RequestDefinitionBuilder> definition
    ) {
        return options(uri, ConsumerWithDelegate.create(definition));
    }

    TestDefinitionBuilder options(CharSequence uri, Consumer<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder options(CharSequence uri);

    default TestDefinitionBuilder trace(
        CharSequence uri,
        @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.ResponseDefinitionBuilder")
            Closure<RequestDefinitionBuilder> definition
    ) {
        return trace(uri, ConsumerWithDelegate.create(definition));
    }

    TestDefinitionBuilder trace(CharSequence uri, Consumer<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder trace(CharSequence uri);

    default TestDefinitionBuilder get(
        CharSequence uri,
        @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.ResponseDefinitionBuilder")
            Closure<RequestDefinitionBuilder> definition
    ) {
        return get(uri, ConsumerWithDelegate.create(definition));
    }

    TestDefinitionBuilder get(CharSequence uri, Consumer<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder get(CharSequence uri);

    TestDefinitionBuilder baseUri(String uri);
}
