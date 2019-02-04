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

    TestDefinitionBuilder head(CharSequence uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder head(CharSequence uri);

    TestDefinitionBuilder post(CharSequence uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder post(CharSequence uri);

    TestDefinitionBuilder put(CharSequence uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder put(CharSequence uri);

    TestDefinitionBuilder patch(CharSequence uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder patch(CharSequence uri);

    TestDefinitionBuilder delete(CharSequence uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder delete(CharSequence uri);

    TestDefinitionBuilder options(CharSequence uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder options(CharSequence uri);

    TestDefinitionBuilder trace(CharSequence uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder trace(CharSequence uri);

    TestDefinitionBuilder get(CharSequence uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition);

    TestDefinitionBuilder get(CharSequence uri);

    TestDefinitionBuilder baseUri(String uri);
}
