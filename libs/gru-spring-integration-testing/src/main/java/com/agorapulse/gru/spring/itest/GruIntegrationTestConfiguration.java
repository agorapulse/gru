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
package com.agorapulse.gru.spring.itest;

import com.agorapulse.gru.Gru;
import com.agorapulse.gru.http.Http;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class GruIntegrationTestConfiguration {

    @Bean(destroyMethod = "close")
    @Scope("prototype")
    public static Gru gru(@Value("${local.server.port}") int serverPort, InjectionPoint injectionPoint) {
        return Gru.create(Http.create(injectionPoint.getMember().getDeclaringClass())).prepare("http://localhost:" + serverPort);
    }

}
