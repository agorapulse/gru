package com.agorapulse.gru;

import com.agorapulse.gru.minions.Command;
import com.agorapulse.gru.minions.Minion;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class DefaultTestDefinitionBuilder implements TestDefinitionBuilder {

    DefaultTestDefinitionBuilder(Client client, Squad squad) {
        this.client = client;
        this.squad = squad;
        this.requestDefinitionBuilder = new DefaultRequestDefinitionBuilder(squad);
        this.responseDefinitionBuilder = new DefaultResponseDefinitionBuilder(squad);
    }

    /**
     * @see Squad#command(Class, Closure)
     */
    @Override
    public <M extends Minion> TestDefinitionBuilder command(@DelegatesTo.Target Class<M> minionType, @DelegatesTo(genericTypeIndex = 0, strategy = Closure.DELEGATE_FIRST) Closure command) {
        this.squad.command(minionType, command);
        return this;
    }

    /**
     * @see com.agorapulse.gru.Squad#command(Class, Command)
     */
    public <M extends Minion> TestDefinitionBuilder command(Class<M> minionType, Command<M> command) {
        this.squad.command(minionType, command);
        return this;
    }

    /**
     * Define expectations about the response after controller action being executed.
     *
     * @param definition expectations about the response after controller action being executed
     * @return self
     */
    @Override
    public TestDefinitionBuilder expect(@DelegatesTo(value = ResponseDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<ResponseDefinitionBuilder> definition) {
        configure(client.getUnitTest(), responseDefinitionBuilder, definition);
        return this;
    }

    /**
     * Mocks head request and verifies controller action is mapped to given URI.
     *
     * @param uri        URI to which should be the controller action mapped
     * @param definition additional request customisation
     * @return self
     */
    @Override
    public TestDefinitionBuilder head(String uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition) {
        return request(uri, HEAD, definition);
    }

    /**
     * Mocks head request and verifies controller action is mapped to given URI.
     *
     * @param uri URI to which should be the controller action mapped
     * @return self
     */
    @Override
    public TestDefinitionBuilder head(String uri) {
        return request(uri, HEAD);
    }

    /**
     * Mocks post request and verifies controller action is mapped to given URI.
     *
     * @param uri        URI to which should be the controller action mapped
     * @param definition additional request customisation
     * @return self
     */
    @Override
    public TestDefinitionBuilder post(String uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition) {
        return request(uri, POST, definition);
    }

    /**
     * Mocks post request and verifies controller action is mapped to given URI.
     *
     * @param uri URI to which should be the controller action mapped
     * @return self
     */
    @Override
    public TestDefinitionBuilder post(String uri) {
        return request(uri, POST);
    }

    /**
     * Mocks put request and verifies controller action is mapped to given URI.
     *
     * @param uri        URI to which should be the controller action mapped
     * @param definition additional request customisation
     * @return self
     */
    @Override
    public TestDefinitionBuilder put(String uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition) {
        return request(uri, PUT, definition);
    }

    /**
     * Mocks put request and verifies controller action is mapped to given URI.
     *
     * @param uri URI to which should be the controller action mapped
     * @return self
     */
    @Override
    public TestDefinitionBuilder put(String uri) {
        return request(uri, PUT);
    }

    /**
     * Mocks patch request and verifies controller action is mapped to given URI.
     *
     * @param uri        URI to which should be the controller action mapped
     * @param definition additional request customisation
     * @return self
     */
    @Override
    public TestDefinitionBuilder patch(String uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition) {
        return request(uri, PATCH, definition);
    }

    /**
     * Mocks patch request and verifies controller action is mapped to given URI.
     *
     * @param uri URI to which should be the controller action mapped
     * @return self
     */
    @Override
    public TestDefinitionBuilder patch(String uri) {
        return request(uri, PATCH);
    }

    /**
     * Mocks delete request and verifies controller action is mapped to given URI.
     *
     * @param uri        URI to which should be the controller action mapped
     * @param definition additional request customisation
     * @return self
     */
    @Override
    public TestDefinitionBuilder delete(String uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition) {
        return request(uri, DELETE, definition);
    }

    /**
     * Mocks delete request and verifies controller action is mapped to given URI.
     *
     * @param uri URI to which should be the controller action mapped
     * @return self
     */
    @Override
    public TestDefinitionBuilder delete(String uri) {
        return request(uri, DELETE);
    }

    /**
     * Mocks options request and verifies controller action is mapped to given URI.
     *
     * @param uri        URI to which should be the controller action mapped
     * @param definition additional request customisation
     * @return self
     */
    @Override
    public TestDefinitionBuilder options(String uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition) {
        return request(uri, OPTIONS, definition);
    }

    /**
     * Mocks options request and verifies controller action is mapped to given URI.
     *
     * @param uri URI to which should be the controller action mapped
     * @return self
     */
    @Override
    public TestDefinitionBuilder options(String uri) {
        return request(uri, OPTIONS);
    }

    /**
     * Mocks trace request and verifies controller action is mapped to given URI.
     *
     * @param uri        URI to which should be the controller action mapped
     * @param definition additional request customisation
     * @return self
     */
    @Override
    public TestDefinitionBuilder trace(String uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition) {
        return request(uri, TRACE, definition);
    }

    /**
     * Mocks trace request and verifies controller action is mapped to given URI.
     *
     * @param uri URI to which should be the controller action mapped
     * @return self
     */
    @Override
    public TestDefinitionBuilder trace(String uri) {
        return request(uri, TRACE);
    }

    /**
     * Mocks get request and verifies controller action is mapped to given URI.
     *
     * @param uri        URI to which should be the controller action mapped
     * @param definition additional request customisation
     * @return self
     */
    @Override
    public TestDefinitionBuilder get(String uri, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition) {
        return request(uri, GET, definition);
    }

    /**
     * Mocks get request and verifies controller action is mapped to given URI.
     *
     * @param uri URI to which should be the controller action mapped
     * @return self
     */
    @Override
    public TestDefinitionBuilder get(String uri) {
        return request(uri, GET);
    }

    @Override
    public TestDefinitionBuilder baseUri(String uri) {
        client.getRequest().setBaseUri(uri);
        return this;
    }

    private TestDefinitionBuilder request(String aUrl, String aMethod, @DelegatesTo(value = RequestDefinitionBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<RequestDefinitionBuilder> definition) {
        client.getRequest().setUri(aUrl);
        client.getRequest().setMethod(aMethod);
        configure(client.getUnitTest(), requestDefinitionBuilder, definition);
        return this;
    }

    @SuppressWarnings("unchecked")
    private TestDefinitionBuilder request(String uri, String method) {
        return request(uri, method, Closure.IDENTITY);
    }

    private static <T> T configure(Object spec, Object delegate, Closure<T> closure) {
        Closure<T> clonedClosure = closure.rehydrate(delegate, spec, closure.getThisObject());
        clonedClosure.setResolveStrategy(Closure.DELEGATE_FIRST);
        return clonedClosure.call(delegate);
    }

    private final Client client;
    private final Squad squad;
    private final RequestDefinitionBuilder requestDefinitionBuilder;
    private final ResponseDefinitionBuilder responseDefinitionBuilder;
}
