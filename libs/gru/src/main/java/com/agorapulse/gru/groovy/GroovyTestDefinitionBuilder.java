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
package com.agorapulse.gru.groovy;

import com.agorapulse.gru.RequestDefinitionBuilder;
import com.agorapulse.gru.ResponseDefinitionBuilder;
import com.agorapulse.gru.TestDefinitionBuilder;
import com.agorapulse.gru.minions.Command;
import com.agorapulse.gru.minions.Minion;
import groovy.lang.GroovyObject;
import org.codehaus.groovy.runtime.StringGroovyMethods;

import java.util.function.Consumer;

public class GroovyTestDefinitionBuilder implements TestDefinitionBuilder {

    private final TestDefinitionBuilder delegate;
    private final Object owner;

    public GroovyTestDefinitionBuilder(TestDefinitionBuilder delegate, Object owner) {
        this.delegate = delegate;
        this.owner = owner;
    }


    @Override
    public TestDefinitionBuilder get(String uri) {
        if (uri.startsWith("/")) {
            return delegate.get(uri);
        }

        if (owner instanceof GroovyObject) {
            GroovyObject groovyObject = (GroovyObject) owner;
            Object property = groovyObject.getProperty(uri);
            throw new IllegalArgumentException(
                "Property '"
                    + uri
                    + "' cannot be reached from the test definition. Please, create a local variable before the test definition:\n\n"
                    + property.getClass().getSimpleName()
                    + " a"
                    + StringGroovyMethods.capitalize((CharSequence) uri)
                    + " = "
                    + uri
                    + "\n"
            );
        }

        return delegate.get(uri);
    }

    @Override
    public <M extends Minion> TestDefinitionBuilder command(Class<M> minionType, Command<M> command) {
        return delegate.command(minionType, command);
    }

    @Override
    public TestDefinitionBuilder expect(Consumer<ResponseDefinitionBuilder> definition) {
        return delegate.expect(definition);
    }

    @Override
    public TestDefinitionBuilder head(String uri, Consumer<RequestDefinitionBuilder> definition) {
        return delegate.head(uri, definition);
    }

    @Override
    public TestDefinitionBuilder head(String uri) {
        return delegate.head(uri);
    }

    @Override
    public TestDefinitionBuilder post(String uri, Consumer<RequestDefinitionBuilder> definition) {
        return delegate.post(uri, definition);
    }

    @Override
    public TestDefinitionBuilder post(String uri) {
        return delegate.post(uri);
    }

    @Override
    public TestDefinitionBuilder put(String uri, Consumer<RequestDefinitionBuilder> definition) {
        return delegate.put(uri, definition);
    }

    @Override
    public TestDefinitionBuilder put(String uri) {
        return delegate.put(uri);
    }

    @Override
    public TestDefinitionBuilder patch(String uri, Consumer<RequestDefinitionBuilder> definition) {
        return delegate.patch(uri, definition);
    }

    @Override
    public TestDefinitionBuilder patch(String uri) {
        return delegate.patch(uri);
    }

    @Override
    public TestDefinitionBuilder delete(String uri, Consumer<RequestDefinitionBuilder> definition) {
        return delegate.delete(uri, definition);
    }

    @Override
    public TestDefinitionBuilder delete(String uri) {
        return delegate.delete(uri);
    }

    @Override
    public TestDefinitionBuilder options(String uri, Consumer<RequestDefinitionBuilder> definition) {
        return delegate.options(uri, definition);
    }

    @Override
    public TestDefinitionBuilder options(String uri) {
        return delegate.options(uri);
    }

    @Override
    public TestDefinitionBuilder trace(String uri, Consumer<RequestDefinitionBuilder> definition) {
        return delegate.trace(uri, definition);
    }

    @Override
    public TestDefinitionBuilder trace(String uri) {
        return delegate.trace(uri);
    }

    @Override
    public TestDefinitionBuilder get(String uri, Consumer<RequestDefinitionBuilder> definition) {
        return delegate.get(uri, definition);
    }

    @Override
    public TestDefinitionBuilder baseUri(String uri) {
        return delegate.baseUri(uri);
    }
}
