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
import com.agorapulse.gru.minions.HttpMinion;
import com.agorapulse.gru.minions.Minion;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Gru steals the controller's unit test to verify controller's actions in context.
 * <p>
 */
public class Gru implements Closeable {

    /**
     * Steals the unit test for himself.
     * <p>
     * Typical usage is <code>Gru gru = Gru.create(this)</code>
     *
     * @param client unit test being stolen
     * @return new Gru instance stealing current unit test
     * @deprecated use {@link #create(Client)} instead
     */
    @Deprecated
    public static Gru equip(Client client) {
        return create(client);
    }

    /**
     * Creates Gru's instance for a given unit test.
     * <p>
     * Typical usage is <code>@AutoCleanup Gru gru = Gru.create(this)</code>
     *
     * @param client unit test
     * @return new Gru instance using the current unit test
     */
    public static Gru create(Client client) {
        return new Gru(client);
    }

    private Gru(Client client) {
        this.client = client;
    }

    /**
     * Prepare every test with following configuration.
     *
     * @param configuration configuration applied to every feature method
     * @return self
     */
    public final Gru prepare(Consumer<TestDefinitionBuilder> configuration
    ) {
        this.configurations.add(configuration);
        return this;
    }


    /**
     * Prepare tests with given base URI.
     *
     * @param baseUri the base URI for the test calls
     * @return self
     */
    public final Gru prepare(String baseUri) {
        this.configurations.add(c -> c.baseUri(baseUri));
        return this;
    }

    /**
     * Adds minion to the squad for current test.
     *
     * @param minion minion to be added to the squad
     * @return self
     */
    public final Gru engage(Minion minion) {
        this.squad.add(minion);
        return this;
    }


    /**
     * Checks if the expectations has been verified and resets the internal state.
     */
    @Override
    public void close() {
        if (!verified) {
            context.throwErrorIfPresent();
            if (definition) {
                throw new AssertionError("Test wasn't verified. Call assertion.verify() from the then block manually!");
            }
        }

        reset(false);
    }

    /**
     * Defines and verifies API test and runs the controller initialization and the action under test.
     * <p>
     *
     * @param expectation test definition
     */
    public final void verify(Consumer<TestDefinitionBuilder> expectation) throws Throwable {
        try (Gru self = test(expectation)) {
            self.verify();
        }
    }

    /**
     * Defines API test and runs the controller initialization and the action under test.
     * <p>
     * Use this method either in when or expect block.
     *
     * @param expectation test definition
     * @return self, note that when Groovy Truth is evaluated, <code>verify</code> method is called automatically
     */
    public final Gru test(Consumer<TestDefinitionBuilder> expectation) {
        definition = true;

        DefaultTestDefinitionBuilder builder = new DefaultTestDefinitionBuilder(client, this.squad);

        for (Minion minion : client.getInitialSquad()) {
            squad.add(minion);
        }

        squad.command(HttpMinion.class, Command.noop());

        for (Consumer<TestDefinitionBuilder> configuration : configurations) {
            configuration.accept(builder);
        }

        expectation.accept(builder);

        checkExpectationsPresent();

        context = squad.beforeRun(client, context);

        if (!context.hasError()) {
            try {
                context = client.run(squad, context);
            } catch (Exception e) {
                context = context.withError(e);
            }
        }

        context = squad.afterRun(client, context);

        return this;
    }

    /**
     * Verifies all expectations.
     *
     * @return true if all verifications are successful
     * @throws AssertionError if any verification fails
     */
    public final boolean verify() throws Throwable {
        checkExpectationsPresent();

        if (verified) {
            return verificationResult;
        }

        verified = true;

        context.throwErrorIfPresent();

        squad.verify(client, context);

        return verificationResult = true;
    }

    private void checkExpectationsPresent() {
        if (!definition) {
            throw new AssertionError("There are no expectations!");
        }
    }

    /**
     * Reset the internal state. This is done by the rule automatically.
     */
    public Gru reset() {
        return reset(true);
    }

    /**
     * Reset the internal state. This is done by the rule automatically.
     *
     * @param resetConfigurations also clear the configurations created using {@link #prepare(Consumer)} method
     */
    public Gru reset(boolean resetConfigurations) {
        verified = false;
        verificationResult = false;
        definition = false;
        context = GruContext.EMPTY;
        squad = new Squad();
        client.reset();

        if (resetConfigurations) {
            configurations.clear();
        }

        return this;
    }

    private final Client client;
    /**
     * Additional configurations to be applied to every feature method.
     */
    private final List<Consumer<TestDefinitionBuilder>> configurations = new ArrayList<>();
    /**
     * Squad for current feature method.
     */
    private Squad squad = new Squad();
    /**
     * Context for current feature method.
     */
    private GruContext context = GruContext.EMPTY;
    /**
     * If the expectations has been already verified.
     */
    private boolean verified;

    /**
     * Verification result.
     */
    private boolean verificationResult;

    /**
     * IF test method has been called
     */
    private boolean definition;
}
