package com.agorapulse.gru;

import com.agorapulse.gru.minions.Command;
import com.agorapulse.gru.minions.HttpMinion;
import com.agorapulse.gru.minions.Minion;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * Gru steals the controller's unit test to verify controller's actions in context.
 * <p>
 * Must be use as JUnit's <code>@Rule</code>.
 *
 * @param <C> type of the client
 */
public class Gru<C extends Client> implements TestRule {
    /**
     * Steals the unit test for himself.
     * <p>
     * Typical usage is <code>@Rule Gru gru = Gru.steal(this)</code>
     *
     * @param client unit test being stolen
     * @return new Gru instance stealing current unit test
     */
    public static <C extends Client> Gru<C> equip(C client) {
        return new Gru<C>(client);
    }

    private Gru(C client) {
        this.client = client;
    }

    /**
     * Prepare every test with following configuration.
     *
     * @param configuration configuration applied to every feature method
     * @return self
     */
    public final Gru prepare(@DelegatesTo(value = TestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<TestDefinitionBuilder> configuration) {
        this.configurations.add(configuration);
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
     * Handles the fresh test configuration for each feature method and verifies the test has been verified.
     *
     * @param base        base statement
     * @param description description of the statement
     * @return new statement which handles the fresh test configuration for each feature method and verifies the test has been verified
     */
    @Override
    public final Statement apply(Statement base, Description description) {
        return new GruStatement(base, this);
    }

    /**
     * Defines API test and runs the controller initialization and the action under test.
     * <p>
     * Use this method either in when or expect block.
     *
     * @param expectation test definition
     * @return self, note that when Groovy Truth is evaluated, <code>verify</code> method is called automatically
     */
    public final Gru test(@DelegatesTo(value = TestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<TestDefinitionBuilder> expectation) {
        definition = true;

        DefaultTestDefinitionBuilder builder = new DefaultTestDefinitionBuilder(client, this.squad);

        for (Minion minion: client.getInitialSquad()) {
            squad.add(minion);
        }

        squad.command(HttpMinion.class, Command.NOOP);

        for (Closure<TestDefinitionBuilder> configuration: configurations) {
            DefaultGroovyMethods.with(builder, configuration);
        }

        DefaultGroovyMethods.with(builder, expectation);

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
     * Allows to create feature method with only "expect" block by calling the verify method when this definition is
     * automagically converted to boolean.
     *
     * @return true if all verifications are successful
     * @throws AssertionError if any verification fails
     */
    public final boolean asBoolean() throws Throwable {
        return verify();
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
     * @param resetConfigurations also clear the configurations created using {@link #prepare(Closure)} method
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

    private final C client;
    /**
     * Additional configurations to be applied to every feature method.
     */
    private List<Closure<TestDefinitionBuilder>> configurations = new ArrayList<Closure<TestDefinitionBuilder>>();
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

    private static class GruStatement extends Statement {
        GruStatement(Statement base, Gru assertion) {
            this.base = base;
            this.assertion = assertion;
        }

        @Override
        public void evaluate() throws Throwable {
            base.evaluate();
            // automatically verify?
            if (!assertion.verified) {
                assertion.context.throwErrorIfPresent();
                if (assertion.definition) {
                    throw new AssertionError("Test wasn't verified. Call assertion.verify() from the then block manually!");
                }

            }

            assertion.reset();
        }

        private final Statement base;
        private final Gru assertion;
    }
}
