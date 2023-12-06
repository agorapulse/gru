/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2023 Agorapulse.
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
import com.agorapulse.gru.Gru;
import com.agorapulse.gru.http.Http;
import com.agorapulse.gru.micronaut.http.MicronautHttpClient;
import com.agorapulse.gru.okhttp.OkHttp;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;

import io.micronaut.context.annotation.Secondary;
import io.micronaut.http.client.HttpClient;
import jakarta.inject.Singleton;

/**
 * Factory which creates Gru instance for the classes annotated with @MicronautTest.
 */
@Factory
@Requires(property = GruFactory.TEST_CLASS_PROPERTY_NAME)
public class GruFactory {

    public static final String TEST_CLASS_PROPERTY_NAME = "micronaut.test.active.spec.clazz";

    @Singleton
    @Bean(preDestroy = "close")
    public Gru gru(Client client) {
        return Gru.create(client);
    }

    @Singleton
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Requires(property = "gru.http.client", notEquals = "jdk", classes = HttpClient.class)
    public Client client(ApplicationContext context, @io.micronaut.http.client.annotation.Client("gru") HttpClient client) {
        Class testClass = context.getRequiredProperty(TEST_CLASS_PROPERTY_NAME, Class.class);
        return Micronaut.createLazy(
            test -> MicronautHttpClient.create(test, client),
            () -> context.getBean(testClass),
            () -> context
        );
    }

    @Singleton
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Requires(property = "gru.http.client", value = "okhttp", classes = OkHttp.class)
    public Client okHttpClient(ApplicationContext context) {
        Class testClass = context.getRequiredProperty(TEST_CLASS_PROPERTY_NAME, Class.class);
        return Micronaut.createLazy(OkHttp::create,
            () -> context.getBean(testClass),
            () -> context
        );
    }

    @Singleton
    @Secondary
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Client jdkHttpClient(ApplicationContext context) {
        Class testClass = context.getRequiredProperty(TEST_CLASS_PROPERTY_NAME, Class.class);
        return Micronaut.createLazy(Http::create,
            () -> context.getBean(testClass),
            () -> context
        );
    }

    @Singleton
    @Bean(preDestroy = "close")
    @Requires(classes = com.agorapulse.gru.kotlin.Gru.class)
    com.agorapulse.gru.kotlin.Gru kotlinGru(Gru gru) {
        return new com.agorapulse.gru.kotlin.Gru(gru);
    }

}
