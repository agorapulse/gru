package com.agorapulse.gru;

import com.agorapulse.gru.minions.*;
import com.google.common.collect.ImmutableMultimap;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

import java.util.Map;

/**
 * Prepares the request for controller action.
 *
 */
public final class DefaultRequestDefinitionBuilder implements RequestDefinitionBuilder {

    protected DefaultRequestDefinitionBuilder(Squad squad) {
        this.squad = squad;
    }

    /**
     * @see Squad#command(Class, Closure)
     */
    public <M extends Minion> DefaultRequestDefinitionBuilder command(@DelegatesTo.Target Class<M> minionType, @DelegatesTo(genericTypeIndex = 0, strategy = Closure.DELEGATE_FIRST) Closure command) {
        this.squad.command(minionType, command);
        return this;
    }

    /**
     * @see Squad#command(Class, Command)
     */
    public <M extends Minion> DefaultRequestDefinitionBuilder command(Class<M> minionType, Command<M> command) {
        this.squad.command(minionType, command);
        return this;
    }

    /**
     * Sets a JSON request from given file.
     * The file must reside in same package as the test in the directory with the same name as the test
     * e.g. src/test/resources/org/example/foo/MySpec.
     * The file is created automatically during first run if it does not exist yet with empty object definition.
     *
     * @param relativePath a JSON request file
     * @return self
     */
    public DefaultRequestDefinitionBuilder json(final String relativePath) {
        return command(JsonMinion.class, new Command<JsonMinion>() {
            @Override
            public void execute(JsonMinion minion) {
                minion.setRequestJsonFile(relativePath);
            }
        });
    }

    /**
     * Adds URL parameters for the action execution.
     *
     * @param params additional URL parameters for the action execution
     * @return self
     */
    public DefaultRequestDefinitionBuilder params(final Map<String, Object> params) {
        return command(ParametersMinion.class, new Command<ParametersMinion>() {
            @Override
            public void execute(ParametersMinion minion) {
                minion.addParameters(params);
            }
        });
    }

    /**
     * Adds HTTP headers for the action execution.
     *
     * @param headers additional HTTP headers for the action execution
     * @return self
     */
    public DefaultRequestDefinitionBuilder headers(final Map<String, String> headers) {
        return command(HttpMinion.class, new Command<HttpMinion>() {
            @Override
            public void execute(HttpMinion minion) {
                minion.getRequestHeaders().putAll(ImmutableMultimap.copyOf(headers.entrySet()));
            }
        });
    }

    private Squad squad;
}
