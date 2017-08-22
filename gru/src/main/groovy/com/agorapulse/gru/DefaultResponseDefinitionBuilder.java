package com.agorapulse.gru;

import com.agorapulse.gru.minions.Command;
import com.agorapulse.gru.minions.HttpMinion;
import com.agorapulse.gru.minions.JsonMinion;
import com.agorapulse.gru.minions.Minion;
import com.google.common.collect.ImmutableMultimap;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import net.javacrumbs.jsonunit.core.Option;
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert;

import java.util.Map;

/**
 * Sets expectations for the response after the controller action has been executed.
 *
 */
public class DefaultResponseDefinitionBuilder implements ResponseDefinitionBuilder {

    protected DefaultResponseDefinitionBuilder(Squad squad) {
        this.squad = squad;
    }

    /**
     * @see Squad#command(Class, Closure)
     */
    public <M extends Minion> DefaultResponseDefinitionBuilder command(Class<M> minionType, @DelegatesTo(type = "M", strategy = Closure.DELEGATE_FIRST) Closure command) {
        this.squad.command(minionType, command);
        return this;
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
        return command(HttpMinion.class, new Command<HttpMinion>() {
            @Override
            public void execute(HttpMinion minion) {
                minion.setStatus(aStatus);
            }
        });
    }

    /**
     * Sets an expected JSON response from given file.
     * The file must reside in same package as the test in the directory with the same name as the test
     * e.g. src/test/resources/org/example/foo/MySpec.
     * The file is created automatically during first run if it does not exist yet with the returned JSON.
     *
     * @param relativePath an expected JSON response file
     * @return self
     */
    public DefaultResponseDefinitionBuilder json(final String relativePath) {
        return command(JsonMinion.class, new Command<JsonMinion>() {
            @Override
            public void execute(JsonMinion minion) {
                minion.setResponseJsonFile(relativePath);
            }
        });
    }

    /**
     * Sets additional assertions and configuration for JsonFluentAssert instance which is testing the response.
     *
     * @param additionalConfiguration additional assertions and configuration for JsonFluentAssert instance
     * @return self
     */
    public DefaultResponseDefinitionBuilder json(@DelegatesTo(value = JsonFluentAssert.class, strategy = Closure.DELEGATE_FIRST) final Closure<JsonFluentAssert> additionalConfiguration) {
        return command(JsonMinion.class, new Command<JsonMinion>() {
            @Override
            public void execute(JsonMinion minion) {
                minion.setJsonUnitConfiguration(additionalConfiguration);
            }
        });
    }

    /**
     * Adds HTTP headers which are expected to be returned after action execution.
     *
     * @param additionalHeaders additional HTTP headers which are expected to be returned after action execution
     * @return self
     */
    public DefaultResponseDefinitionBuilder headers(final Map<String, String> additionalHeaders) {
        return command(HttpMinion.class, new Command<HttpMinion>() {
            @Override
            public void execute(HttpMinion minion) {
                minion.getResponseHeaders().putAll(ImmutableMultimap.copyOf(additionalHeaders.entrySet()));
            }
        });
    }

    /**
     * Sets the expected redirection URI.
     *
     * @param uri expected URI
     * @return self
     */
    public DefaultResponseDefinitionBuilder redirect(final String uri) {
        return command(HttpMinion.class, new Command<HttpMinion>() {
            @Override
            public void execute(HttpMinion minion) {
                minion.setRedirectUri(uri);
                minion.setStatus(FOUND);
            }
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
        return json(new Closure<JsonFluentAssert>(this, this) {
            public JsonFluentAssert doCall(Object it) {
                return ((JsonFluentAssert)it).when(option, options);
            }
        });
    }

    private Squad squad;
}
