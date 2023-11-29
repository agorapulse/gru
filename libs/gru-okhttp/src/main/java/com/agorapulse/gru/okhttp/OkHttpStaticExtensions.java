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
package com.agorapulse.gru.okhttp;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import okhttp3.OkHttpClient;
import space.jasan.support.groovy.closure.ConsumerWithDelegate;

public class OkHttpStaticExtensions {

    public static OkHttp create(OkHttp self, @DelegatesTo(value = OkHttpClient.Builder.class, strategy = Closure.DELEGATE_FIRST) Closure<OkHttpClient.Builder> configuration) {
        return OkHttp.create(configuration.getOwner(), ConsumerWithDelegate.create(configuration));
    }

    @Deprecated
    public static OkHttp steal(OkHttp self, Object unitTest, @DelegatesTo(value = OkHttpClient.Builder.class, strategy = Closure.DELEGATE_FIRST) Closure<OkHttpClient.Builder> configuration) {
        return OkHttp.create(unitTest, ConsumerWithDelegate.create(configuration));
    }

}
