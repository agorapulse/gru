package com.agorapulse.gru;

import com.agorapulse.gru.minions.Command;
import com.agorapulse.gru.minions.Minion;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public interface TestDefinitionBuilder extends HttpVerbsShortcuts {
    /**
     * Shortcut for default url mappings name.
     */
    //CHECKSTYLE:OFF
    String UrlMappings = "UrlMappings";
    //CHECKSTYLE:ON

    /**
     * @see Squad#command(Class, Closure)
     */
    <M extends Minion> TestDefinitionBuilder command(@DelegatesTo.Target Class<M> minionType, @DelegatesTo(genericTypeIndex = 0, strategy = Closure.DELEGATE_FIRST) Closure command);

    /**
     * @see Squad#command(Class, Closure)
     */
    <M extends Minion> TestDefinitionBuilder command(Class<M> minionType, Command<M> command);


    TestDefinitionBuilder expect(@DelegatesTo(value = ResponseDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<ResponseDefinitionBuilder> definition);

    TestDefinitionBuilder head(String uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder head(String uri);

    TestDefinitionBuilder post(String uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder post(String uri);

    TestDefinitionBuilder put(String uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder put(String uri);

    TestDefinitionBuilder patch(String uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder patch(String uri);

    TestDefinitionBuilder delete(String uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder delete(String uri);

    TestDefinitionBuilder options(String uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder options(String uri);

    TestDefinitionBuilder trace(String uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder trace(String uri);

    TestDefinitionBuilder get(String uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder get(String uri);

    TestDefinitionBuilder baseUri(String uri);
}
