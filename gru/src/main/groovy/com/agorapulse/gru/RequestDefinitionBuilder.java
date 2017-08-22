package com.agorapulse.gru;

import com.agorapulse.gru.minions.Command;
import com.agorapulse.gru.minions.Minion;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

import java.util.Map;

/**
 * Prepares the request for controller action.
 */
public interface RequestDefinitionBuilder {

    /**
     * @see Squad#command(Class, Closure)
     */
    <M extends Minion> RequestDefinitionBuilder command(@DelegatesTo.Target Class<M> minionType, @DelegatesTo(genericTypeIndex = 0, strategy = Closure.DELEGATE_FIRST) Closure command);

    /**
     * @see Squad#command(Class, Closure)
     */
    <M extends Minion> RequestDefinitionBuilder command(Class<M> minionType, Command<M> command);



    /**
     * Sets a JSON request from given file.
     * The file must reside in same package as the test in the directory with the same name as the test
     * e.g. src/test/resources/org/example/foo/MySpec.
     * The file is created automatically during first run if it does not exist yet with empty object definition.
     *
     * @param relativePath a JSON request file
     * @return self
     */
    RequestDefinitionBuilder json(String relativePath);

    /**
     * Adds URL parameters for the action execution.
     *
     * @param params additional URL parameters for the action execution
     * @return self
     */
    RequestDefinitionBuilder params(Map<String, Object> params);

    /**
     * Adds HTTP headers for the action execution.
     *
     * @param headers additional HTTP headers for the action execution
     * @return self
     */
    RequestDefinitionBuilder headers(Map<String, String> headers);
}
