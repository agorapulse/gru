package com.agorapulse.gru;

import com.agorapulse.gru.minions.Command;
import com.agorapulse.gru.minions.Minion;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import net.javacrumbs.jsonunit.core.internal.JsonUtils;

import java.util.List;
import java.util.Map;

/**
 * Prepares the request for controller action.
 */
public interface RequestDefinitionBuilder extends WithContentSupport {

    /**
     * @see Squad#command(Class, Closure)
     */
    default <M extends Minion> RequestDefinitionBuilder command(@DelegatesTo.Target Class<M> minionType, @DelegatesTo(genericTypeIndex = 0, strategy = Closure.DELEGATE_FIRST) Closure command) {
        return command(minionType, Command.create(command));
    }

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
     * It automatically applies Content-Type: application/json header.
     *
     * @param relativePath a JSON request file
     * @return self
     */
    RequestDefinitionBuilder json(String relativePath);

    /**
     * Sets a JSON request from given content.
     *
     * It automatically applies Content-Type: application/json header.
     *
     * @param content a JSON request file
     * @return self
     */
    RequestDefinitionBuilder json(Content content);

    /**
     * Sets a JSON request from given content.
     *
     * It automatically applies Content-Type: application/json header.
     *
     * @param map map to be converted to JSON
     * @return self
     */
    default RequestDefinitionBuilder json(Map map) {
        return json(inline(JsonUtils.convertToJson(map, "request").toString()));
    }

    /**
     * Sets a JSON request from given content.
     *
     * It automatically applies Content-Type: application/json header.
     *
     * @param list list to be converted to JSON
     * @return self
     */
    default RequestDefinitionBuilder json(List list) {
        return json(inline(JsonUtils.convertToJson(list, "request").toString()));
    }

    /**
     * Sets a payload for the request from given file.
     * The file must reside in same package as the test in the directory with the same name as the test
     * e.g. src/test/resources/org/example/foo/MySpec.
     * The file is created automatically during first run if it does not exist yet with empty object definition.
     *
     * It automatically applies Content-Type header.
     *
     * @param relativePath relative path to the payload file for the request
     * @param contentType content type of the payload
     * @return self
     */
    RequestDefinitionBuilder content(String relativePath, String contentType);

    /**
     * Sets a request payload from given content.
     *
     * It automatically applies Content-Type header.
     *
     * @param content payload for the request
     * @param contentType content type of the payload
     * @return self
     */
    RequestDefinitionBuilder content(Content content, String contentType);

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
