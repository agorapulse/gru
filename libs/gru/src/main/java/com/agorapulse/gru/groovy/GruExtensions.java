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

import com.agorapulse.gru.*;
import com.agorapulse.gru.minions.Command;
import com.agorapulse.gru.minions.Minion;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FromString;
import groovy.transform.stc.SimpleType;
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert;
import org.intellij.lang.annotations.Language;
import space.jasan.support.groovy.closure.ConsumerWithDelegate;
import space.jasan.support.groovy.closure.FunctionWithDelegate;

public class GruExtensions {

    /**
     * Prepare every test with following configuration.
     *
     * @param configuration configuration applied to every feature method
     * @return self
     */
    public static <C extends Client> Gru prepare(
        Gru self,
        @DelegatesTo(value = TestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.TestDefinitionBuilder")
        Closure<TestDefinitionBuilder> configuration
    ) {
        return self.prepare(builder -> ConsumerWithDelegate.create(configuration).accept(new GroovyTestDefinitionBuilder(builder, configuration.getOwner())));
    }


    /**
     * Defines API test and runs the controller initialization and the action under test.
     * <p>
     * Use this method either in when or expect block.
     *
     * @param expectation test definition
     * @return self, note that when Groovy Truth is evaluated, <code>verify</code> method is called automatically
     */
    public static Gru test(
        Gru self,
        @DelegatesTo(value = TestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.TestDefinitionBuilder")
        Closure<TestDefinitionBuilder> expectation
    ) {
        return self.test(builder -> ConsumerWithDelegate.create(expectation).accept(new GroovyTestDefinitionBuilder(builder, expectation.getOwner())));
    }

    /**
     * Defines and verifies API test and runs the controller initialization and the action under test.
     *
     * @param expectation test definition
     */
    public static void verify(
        Gru self,
        @DelegatesTo(value = TestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.TestDefinitionBuilder")
        Closure<TestDefinitionBuilder> expectation
    ) throws Throwable {
        self.verify(builder -> ConsumerWithDelegate.create(expectation).accept(new GroovyTestDefinitionBuilder(builder, expectation.getOwner())));
    }

    /**
     * Allows to create feature method with only "expect" block by calling the verify method when this definition is
     * automagically converted to boolean.
     *
     * @return true if all verifications are successful
     * @throws AssertionError if any verification fails
     */
    public static boolean asBoolean(Gru self) throws Throwable {
        return self.verify();
    }

    /**
     * @see Squad#command(Class, Command)
     */
    public static <M extends Minion> RequestDefinitionBuilder command(
        RequestDefinitionBuilder self,
        @DelegatesTo.Target Class<M> minionType,
        @DelegatesTo(genericTypeIndex = 0, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = FromString.class, options = "M")
        Closure<?> command
    ) {
        return self.command(minionType, createCommand(command));
    }

    public static RequestDefinitionBuilder upload(
        RequestDefinitionBuilder self,
        @DelegatesTo(value = MultipartDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.MultipartDefinitionBuilder")
        Closure<MultipartDefinitionBuilder> definition
    ) {
        return self.upload(ConsumerWithDelegate.create(definition));
    }

    /**
     * @see Squad#command(Class, Command)
     */
    public static <M extends Minion> ResponseDefinitionBuilder command(
        ResponseDefinitionBuilder self,
        Class<M> minionType,
        @DelegatesTo(type = "M", strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = FromString.class, options = "M")
        Closure<?> command
    ) {
        return self.command(minionType, createCommand(command));
    }

    /**
     * Sets additional assertions and configuration for JsonFluentAssert instance which is testing the response.
     *
     * @param additionalConfiguration additional assertions and configuration for JsonFluentAssert instance
     * @return self
     */
    public static ResponseDefinitionBuilder json(
        ResponseDefinitionBuilder self,
        @DelegatesTo(value = JsonFluentAssert.ConfigurableJsonFluentAssert.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "net.javacrumbs.jsonunit.fluent.JsonFluentAssert.ConfigurableJsonFluentAssert")
        Closure<JsonFluentAssert.ConfigurableJsonFluentAssert> additionalConfiguration
    ) {
        return self.json(FunctionWithDelegate.create(additionalConfiguration));
    }


    /**
     * Expect a cookie returned by the server.
     * @param cookieDefinition definition of the cookie
     * @return self
     */
    public static ResponseDefinitionBuilder cookie(
        ResponseDefinitionBuilder self,
        @DelegatesTo(value = ResponseCookieDefinition.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.ResponseCookieDefinition")
        Closure<ResponseCookieDefinition> cookieDefinition
    ) {
        return self.cookie(ConsumerWithDelegate.create(cookieDefinition));
    }

    /**
     * Command minion of given type.
     *
     * If the minion is not yet present in the squad it is instantiated using default constructor.
     * @param minionType type of the minion being commanded
     * @param aCommand closure executed within context of selected minion
     */
    public static <M extends Minion> void command(
        Squad self,
        Class<M> minionType,
        @DelegatesTo(type = "M", strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = FromString.class, options = "M")
        Closure<?> aCommand
    ) {
        self.command(minionType, createCommand(aCommand));
    }

    /**
     * Asks minion of given type for something.
     * @param minionType type of the minion being asked
     * @param query closure executed within context of selected minion which returns the result of this method
     * @return result returned from the query closure or null if minion of given type is not present in the squad
     */
    public static <T, M extends Minion> T ask(
        Squad self,
        Class<M> minionType,
        @DelegatesTo(type = "M", strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = FromString.class, options = "M")
        Closure<T> query
    ) {
        return self.ask(minionType, FunctionWithDelegate.create(query));
    }

    public static TestDefinitionBuilder expect(
        TestDefinitionBuilder self,
        @DelegatesTo(value = ResponseDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.ResponseDefinitionBuilder")
        Closure<ResponseDefinitionBuilder> definition
    ) {
        return self.expect(ConsumerWithDelegate.create(definition));
    }

    /**
     * @see Squad#command(Class, Command)
     */
    public static <M extends Minion> TestDefinitionBuilder command(
        TestDefinitionBuilder self,
        @DelegatesTo.Target Class<M> minionType,
        @DelegatesTo(genericTypeIndex = 0, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = FromString.class, options = "M")
        Closure<?> command
    ) {
        return self.command(minionType, createCommand(command));
    }

    public static TestDefinitionBuilder head(
        TestDefinitionBuilder self,
        @Language("HTTP Request") String uri,
        @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.RequestDefinitionBuilder")
        Closure<RequestDefinitionBuilder> definition
    ) {
        return self.head(uri, ConsumerWithDelegate.create(definition));
    }

    public static TestDefinitionBuilder post(
        TestDefinitionBuilder self,
        @Language("HTTP Request") String uri,
        @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.RequestDefinitionBuilder")
        Closure<RequestDefinitionBuilder> definition
    ) {
        return self.post(uri, ConsumerWithDelegate.create(definition));
    }


    public static TestDefinitionBuilder get(
        TestDefinitionBuilder self,
        @Language("HTTP Request") String uri,
        @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.RequestDefinitionBuilder")
        Closure<RequestDefinitionBuilder> definition
    ) {
        return self.get(uri, ConsumerWithDelegate.create(definition));
    }


    public static TestDefinitionBuilder put(
        TestDefinitionBuilder self,
        @Language("HTTP Request") String uri,
        @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.RequestDefinitionBuilder")
        Closure<RequestDefinitionBuilder> definition
    ) {
        return self.put(uri, ConsumerWithDelegate.create(definition));
    }

    public static TestDefinitionBuilder patch(
        TestDefinitionBuilder self,
        @Language("HTTP Request") String uri,
        @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.RequestDefinitionBuilder")
        Closure<RequestDefinitionBuilder> definition
    ) {
        return self.patch(uri, ConsumerWithDelegate.create(definition));
    }

    public static TestDefinitionBuilder delete(
        TestDefinitionBuilder self,
        @Language("HTTP Request") String uri,
        @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.RequestDefinitionBuilder")
        Closure<RequestDefinitionBuilder> definition
    ) {
        return self.delete(uri, ConsumerWithDelegate.create(definition));
    }

    public static TestDefinitionBuilder options(
        TestDefinitionBuilder self,
        @Language("HTTP Request") String uri,
        @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.RequestDefinitionBuilder")
        Closure<RequestDefinitionBuilder> definition
    ) {
        return self.options(uri, ConsumerWithDelegate.create(definition));
    }

    public static TestDefinitionBuilder trace(
        TestDefinitionBuilder self,
        @Language("HTTP Request") String uri,
        @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.gru.RequestDefinitionBuilder")
        Closure<RequestDefinitionBuilder> definition
    ) {
        return self.trace(uri, ConsumerWithDelegate.create(definition));
    }

    private static <M extends Minion> Command<M> createCommand(Closure<?> closure) {
        return m -> ConsumerWithDelegate.create(closure).accept(m);
    }

}
