/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2021 Agorapulse.
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
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

import javax.inject.Singleton;
import java.util.Optional;

@Factory
public class GruFactory {

    private static final String TEST_CLASS_PROPERTY_NAME = "micronaut.test.active.spec.clazz";

    @Singleton
    @Bean(preDestroy = "close")
    @SuppressWarnings({"rawtypes", "unchecked"})
    Gru<Client> gru(ApplicationContext context) {
        Optional<Class> maybeTestClass = context.getProperty(TEST_CLASS_PROPERTY_NAME, Class.class);

        if (!maybeTestClass.isPresent()) {
            return null;
        }

        return Gru.create(Micronaut.createLazy(() -> context.getBean(maybeTestClass.get()), () -> context));
    }

}
