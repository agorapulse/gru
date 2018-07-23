package com.agorapulse.gru;

import com.agorapulse.gru.content.BuilderContent;
import com.agorapulse.gru.content.FileContent;
import com.agorapulse.gru.content.StringContent;
import com.agorapulse.gru.minions.*;
import com.google.common.collect.ImmutableMultimap;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

import java.util.Collections;
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

    public DefaultRequestDefinitionBuilder json(final Content content) {
        headers(Collections.singletonMap("Content-Type", "application/json"));
        return command(JsonMinion.class, new Command<JsonMinion>() {
            @Override
            public void execute(JsonMinion minion) {
                minion.setRequestJsonContent(content);
            }
        });
    }

    @Override
    public DefaultRequestDefinitionBuilder json(String relativePath) {
        return json(FileContent.create(relativePath));
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

    @Override
    public Content inline(String string) {
        return StringContent.create(string);
    }

    @Override
    public Content build(Closure closure) {
        return BuilderContent.create(closure);
    }

    private Squad squad;
}
