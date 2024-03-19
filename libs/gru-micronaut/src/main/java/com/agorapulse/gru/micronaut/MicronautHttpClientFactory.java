/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2024 Agorapulse.
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
import com.agorapulse.gru.micronaut.http.MicronautHttpClient;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.*;
import io.micronaut.http.client.HttpClient;
import jakarta.inject.Singleton;

/**
 * Factory that creates {@link HttpClient} implementation of the HTTP client.
 */
@Factory
@Requires(property = GruFactory.TEST_CLASS_PROPERTY_NAME)
@Requires(classes = HttpClient.class)
@Requires(missingProperty = "gru.http.client")
@Replaces(factory = JdkClientFactory.class)
public class MicronautHttpClientFactory {

    @Bean
    @Singleton
    @Secondary
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Client client(ApplicationContext context, @io.micronaut.http.client.annotation.Client("gru") HttpClient client) {
        Class testClass = context.getRequiredProperty(GruFactory.TEST_CLASS_PROPERTY_NAME, Class.class);
        return Micronaut.createLazy(
            test -> MicronautHttpClient.create(test, client),
            () -> context.getBean(testClass),
            () -> context
        );
    }


}
