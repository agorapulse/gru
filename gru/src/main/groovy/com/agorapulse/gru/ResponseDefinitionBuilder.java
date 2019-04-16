package com.agorapulse.gru;

import com.agorapulse.gru.minions.Command;
import com.agorapulse.gru.minions.Minion;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import net.javacrumbs.jsonunit.core.Option;
import net.javacrumbs.jsonunit.core.internal.JsonUtils;
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert;
import space.jasan.support.groovy.closure.FunctionWithDelegate;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Sets expectations for the response after the controller action has been executed.
 *
 */
public interface ResponseDefinitionBuilder extends HttpStatusShortcuts, JsonUnitOptionsShortcuts, WithContentSupport {

    /**
     * @see Squad#command(Class, Closure)
     */
    default <M extends Minion> ResponseDefinitionBuilder command(Class<M> minionType, @DelegatesTo(type = "M", strategy = Closure.DELEGATE_FIRST) Closure command) {
        return command(minionType, Command.create(command));
    }

    /**
     * @see Squad#command(Class, Closure)
     */
    <M extends Minion> ResponseDefinitionBuilder command(Class<M> minionType, Command<M> command);

    /**
     * Sets a expected status returned.
     * Defaults to OK.
     *
     * @param aStatus a expected status returned
     * @return self
     */
    ResponseDefinitionBuilder status(int aStatus);

    /**
     * Sets an expected JSON response from given file.
     * The file must reside in same package as the test in the directory with the same name as the test
     * e.g. src/test/resources/org/example/foo/MySpec.
     * The file is created automatically during first run if it does not exist yet with the returned JSON.
     *
     * @param relativePath an expected JSON response file
     * @return self
     */
    ResponseDefinitionBuilder json(String relativePath);

    /**
     * Sets an expected JSON response from given content.
     *
     * @param content direct content
     * @return self
     */
    ResponseDefinitionBuilder json(Content content);

    /**
     * Sets a JSON request from given content.
     *
     * It automatically applies Content-Type: application/json header.
     *
     * @param map map to be converted to JSON
     * @return self
     */
    default ResponseDefinitionBuilder json(Map map) {
        return json(inline(JsonUtils.convertToJson(map, "response").toString()));
    }

    /**
     * Sets a JSON request from given content.
     *
     * It automatically applies Content-Type: application/json header.
     *
     * @param list list to be converted to JSON
     * @return self
     */
    default ResponseDefinitionBuilder json(List list) {
        return json(inline(JsonUtils.convertToJson(list, "response").toString()));
    }


    /**
     * Sets an expected HTML response from given file.
     * The file must reside in same package as the test in the directory with the same name as the test
     * e.g. src/test/resources/org/example/foo/MySpec.
     * The file is created automatically during first run if it does not exist yet with the returned HTML.
     *
     * @param relativePath an expected HTML response file
     * @return self
     */
    ResponseDefinitionBuilder html(String relativePath);

    /**
     * Sets an expected HTML response from given content.
     *
     *  @param content an expected HTML response file
     * @return self
     */
    ResponseDefinitionBuilder html(Content content);

    /**
     * Sets an expected text response from given file.
     * The file must reside in same package as the test in the directory with the same name as the test
     * e.g. src/test/resources/org/example/foo/MySpec.
     * The file is created automatically during first run if it does not exist yet with the returned HTML.
     *
     * @param relativePath an expected HTML response file
     * @return self
     */
    ResponseDefinitionBuilder text(String relativePath);
    /**
     * Sets an expected text response from given content.
     *
     * @param content an expected HTML response file
     * @return self
     */
    ResponseDefinitionBuilder text(Content content);

    /**
     * Sets additional assertions and configuration for JsonFluentAssert instance which is testing the response.
     *
     * @param additionalConfiguration additional assertions and configuration for JsonFluentAssert instance
     * @return self
     */
    default ResponseDefinitionBuilder json(@DelegatesTo(value = JsonFluentAssert.class, strategy = Closure.DELEGATE_FIRST) Closure<JsonFluentAssert> additionalConfiguration) {
        return json(FunctionWithDelegate.create(additionalConfiguration));
    }

    /**
     * Sets additional assertions and configuration for JsonFluentAssert instance which is testing the response.
     *
     * @param additionalConfiguration additional assertions and configuration for JsonFluentAssert instance
     * @return self
     */
    DefaultResponseDefinitionBuilder json(Function<JsonFluentAssert, JsonFluentAssert> additionalConfiguration);

    /**
     * Adds HTTP headers which are expected to be returned after action execution.
     *
     * @param additionalHeaders additional HTTP headers which are expected to be returned after action execution
     * @return self
     */
    ResponseDefinitionBuilder headers(Map<String, String> additionalHeaders);

    /**
     * Sets the expected redirection URI.
     *
     * @param uri expected URI
     * @return self
     */
    ResponseDefinitionBuilder redirect(String uri);

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
    ResponseDefinitionBuilder json(String relativePath, Option option, Option... options);
}
