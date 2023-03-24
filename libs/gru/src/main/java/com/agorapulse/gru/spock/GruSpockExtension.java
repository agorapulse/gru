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
package com.agorapulse.gru.spock;

import com.agorapulse.gru.Gru;
import org.spockframework.runtime.extension.IGlobalExtension;
import org.spockframework.runtime.model.SpecInfo;

public class GruSpockExtension implements IGlobalExtension {

    @Override
    public void visitSpec(SpecInfo spec) {
        spec.getAllFields().stream().filter(f -> Gru.class.isAssignableFrom(f.getType())).findAny().ifPresent(fieldInfo -> {
            spec.getAllFeatures().forEach(feature -> feature.getFeatureMethod().addInterceptor(invocation -> {
                try (Gru ignored = (Gru) fieldInfo.readValue(invocation.getInstance())) {
                    invocation.proceed();
                }
            }));
        });
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

}
