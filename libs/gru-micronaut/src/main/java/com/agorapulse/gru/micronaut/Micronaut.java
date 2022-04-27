/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2022 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agorapulse.gru.micronaut;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.LazyClient;
import com.agorapulse.gru.Squad;
import com.agorapulse.gru.http.Http;
import com.agorapulse.gru.minions.Minion;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.context.ApplicationContextProvider;
import io.micronaut.runtime.server.EmbeddedServer;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Client for Micronaut applications.
 *
 * This uses {@link Http} client to interact with {@link EmbeddedServer}.
 */
public class Micronaut implements Client {

    /**
     * {@link ApplicationContext} build helper.
     */
    public static class MicronautApplicationBuilder {
        private final Object unitTest;
        private Consumer<ApplicationContextBuilder> contextBuilderConfiguration = b -> {};
        private Consumer<ApplicationContext> contextConfiguration = b -> {};

        MicronautApplicationBuilder(Object unitTest) {
            this.unitTest = unitTest;
        }

        /**
         * Customize the application context.
         * @param contextConfiguration application context configuration
         * @return self
         */
        public MicronautApplicationBuilder doWithContext(Consumer<ApplicationContext> contextConfiguration) {
            this.contextConfiguration = this.contextConfiguration.andThen(contextConfiguration);
            return this;
        }


        /**
         * Customize the application context builder.
         * @param contextBuilderConfiguration application context configuration
         * @return self
         */
        public MicronautApplicationBuilder doWithContextBuilder(Consumer<ApplicationContextBuilder> contextBuilderConfiguration) {
            this.contextBuilderConfiguration = this.contextBuilderConfiguration.andThen(contextBuilderConfiguration);
            return this;
        }

        /**
         * Starts the internal {@link ApplicationContext} and returns the client instance.
         * @return the new client instance
         */
        public Client start() {
            SelfStartingContextProvider provider = new SelfStartingContextProvider(this);
            provider.getApplicationContext().inject(unitTest);
            return create(unitTest, provider);
        }
    }

    private static class SelfStartingContextProvider implements ApplicationContextProvider, Closeable {

        private final MicronautApplicationBuilder micronautApplicationBuilder;
        private ApplicationContext context;

        private SelfStartingContextProvider(MicronautApplicationBuilder builder) {
            this.micronautApplicationBuilder = builder;
        }

        @Override
        public ApplicationContext getApplicationContext() {
            if (context != null) {
                return context;
            }

            ApplicationContextBuilder builder = ApplicationContext.builder();
            micronautApplicationBuilder.contextBuilderConfiguration.accept(builder);

            context = builder.build();
            micronautApplicationBuilder.contextConfiguration.accept(context);

            context.start();

            EmbeddedServer application = context.getBean(EmbeddedServer.class);

            application.start();

            return context;
        }

        @Override
        public void close() {
            context.close();
        }
    }

    private final ApplicationContextProvider contextProvider;
    private final Client delegate;

    /**
     * Starts building a client using a custom {@link ApplicationContext} for this test.
     *
     * @param unitTest the current test
     * @return a builder for a client using a custom {@link ApplicationContext}
     */
    public static MicronautApplicationBuilder build(Object unitTest) {
        return new MicronautApplicationBuilder(unitTest);
    }

    /**
     * Creates a new client either reusing the existing application context if the unit test
     * implements {@link ApplicationContextProvider} or starting a new custom one.
     *
     * @param unitTest the current test
     * @return a builder for a client using either existing or a custom {@link ApplicationContext}
     */
    public static Client create(Object unitTest) {
        if (unitTest instanceof ApplicationContextProvider) {
            return new Micronaut((ApplicationContextProvider) unitTest, Http.create(unitTest));
        }
        assertProviderIfPossible(unitTest);
        return build(unitTest).start();
    }

    /**
     * Creates a lazy initialized client using the provided application context.
     *
     * @param unitTestProvider the current test provider
     * @param provider the context provider
     * @return a builder for a client using either existing or a custom {@link ApplicationContext}
     */
    public static Client createLazy(Supplier<?> unitTestProvider, ApplicationContextProvider provider) {
        return new Micronaut(provider, LazyClient.create(() -> Http.create(unitTestProvider.get())));
    }

    /**
     * Creates a new client using the provided application context.
     *
     * @param unitTest the current test
     * @param provider the context provider
     * @return a builder for a client using either existing or a custom {@link ApplicationContext}
     */
    public static Client create(Object unitTest, ApplicationContextProvider provider) {
        return new Micronaut(provider, Http.create(unitTest));
    }

    private Micronaut(ApplicationContextProvider contextProvider, Client delegate) {
        this.contextProvider = contextProvider;
        this.delegate = delegate;
    }

    @Override
    public Request getRequest() {
        return delegate.getRequest();
    }

    @Override
    public Response getResponse() {
        return delegate.getResponse();
    }

    @Override
    public void reset() {
        if (contextProvider instanceof Closeable) {
            try {
                ((Closeable) contextProvider).close();
            } catch (IOException exception) {
                throw new IllegalStateException("Cannot close " + contextProvider, exception);
            }
        }
        delegate.reset();
    }

    @Override
    public GruContext run(Squad squad, GruContext context) {
        String baseUrl = contextProvider.getApplicationContext().getBean(EmbeddedServer.class).getURL().toExternalForm();
        delegate.getRequest().setBaseUri(baseUrl);
        return delegate.run(squad, context);
    }

    @Override
    public InputStream loadFixture(String fileName) {
        return delegate.loadFixture(fileName);
    }

    @Override
    public String getCurrentDescription() {
        return delegate.getCurrentDescription();
    }

    @Override
    public List<Minion> getInitialSquad() {
        return delegate.getInitialSquad();
    }

    @Override
    public Object getUnitTest() {
        return delegate.getUnitTest();
    }

    private static void assertProviderIfPossible(Object unitTest) {
        if (unitTest == null || unitTest instanceof ApplicationContextProvider) {
            return;
        }

        if (Arrays.stream(unitTest.getClass().getAnnotations()).anyMatch(a -> a.getClass().getSimpleName().equals("MicronautTest"))) {
            throw new IllegalArgumentException(
                "Specification "
                    + unitTest.getClass().getName()
                    + " must either implement "
                    + ApplicationContextProvider.class.getName()
                    + " or use inject the instance using @Inject Gru gru"
            );
        }

        Optional<Field> contextField = Arrays.stream(unitTest.getClass().getFields()).filter(f -> ApplicationContext.class.isAssignableFrom(f.getType())).findFirst();
        if (contextField.isPresent()) {
            throw new IllegalArgumentException(
                "Specification "
                    + unitTest.getClass().getName()
                    + " must either implement "
                    + ApplicationContextProvider.class.getName()
                    + " returning value of field "
                    + contextField.get().getName()
                    + " or use Micronaut.create(this) { "
                    + contextField.get().getName()
                    + " } instead of Micronaut.create(this)"
            );
        }
    }

}
