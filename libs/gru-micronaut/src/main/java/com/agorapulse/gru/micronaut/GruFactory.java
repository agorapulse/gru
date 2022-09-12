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

import com.agorapulse.gru.Gru;
import com.agorapulse.gru.http.Http;
import com.agorapulse.gru.micronaut.okhttp3.MicronautGruConfiguration;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;

import javax.inject.Singleton;
import java.util.List;

/**
 * Factory which creates Gru instance for the classes annotated with @MicronautTest.
 */
@Factory
@Requires(property = GruFactory.TEST_CLASS_PROPERTY_NAME)
public class GruFactory {

    public static final String TEST_CLASS_PROPERTY_NAME = "micronaut.test.active.spec.clazz";

    @Singleton
    @Bean(preDestroy = "close")
    @SuppressWarnings({"rawtypes", "unchecked"})
    Gru gru(ApplicationContext context, List<MicronautGruConfiguration> httpConfiguration) {
        Class testClass = context.getRequiredProperty(TEST_CLASS_PROPERTY_NAME, Class.class);
        return Gru.create(
            Micronaut.createLazy(unitTest ->
                Http.create(
                    unitTest,
                    builder -> httpConfiguration.forEach(conf -> conf.accept(builder))
                ),
                () -> context.getBean(testClass),
                () -> context
            )
        );
    }

    @Singleton
    @Bean(preDestroy = "close")
    @Requires(classes = com.agorapulse.gru.kotlin.Gru.class)
    com.agorapulse.gru.kotlin.Gru kotlinGru(Gru gru) {
        return new com.agorapulse.gru.kotlin.Gru(gru);
    }

}
