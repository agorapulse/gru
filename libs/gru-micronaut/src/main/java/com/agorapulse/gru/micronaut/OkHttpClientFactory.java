/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2025 Agorapulse.
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
import com.agorapulse.gru.okhttp.OkHttp;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

/**
 * Factory that creates OkHttp implementation of the HTTP client.
 */
@Factory
@Requires(property = GruFactory.TEST_CLASS_PROPERTY_NAME)
@Requires(classes = OkHttp.class)
@Replaces(factory = JdkClientFactory.class)
@Requires(property = "gru.http.client", value = "okhttp", classes = OkHttp.class)
public class OkHttpClientFactory {

    @Bean
    @Singleton
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Requires(property = "gru.http.client", value = "okhttp", classes = OkHttp.class)
    public Client okHttpClient(ApplicationContext context) {
        Class testClass = context.getRequiredProperty(GruFactory.TEST_CLASS_PROPERTY_NAME, Class.class);
        return Micronaut.createLazy(OkHttp::create,
            () -> context.getBean(testClass),
            () -> context
        );
    }

}
