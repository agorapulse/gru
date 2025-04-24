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
package com.agorapulse.gru.micronaut.http;

import io.micronaut.context.annotation.Secondary;
import io.micronaut.http.client.DefaultHttpClientConfiguration;
import io.micronaut.http.client.HttpClientConfiguration;
import io.micronaut.runtime.ApplicationConfiguration;
import jakarta.inject.Named;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static java.lang.management.ManagementFactory.getRuntimeMXBean;

@Secondary
@Named("gru")
public class MicronautHttpClientConfiguration extends HttpClientConfiguration {

    public MicronautHttpClientConfiguration(ApplicationConfiguration applicationConfiguration) {
        super(applicationConfiguration);
        configure();
    }

    public MicronautHttpClientConfiguration() {
        configure();
    }

    public MicronautHttpClientConfiguration(HttpClientConfiguration copy) {
        super(copy);
        configure();
    }


    @Override
    public ConnectionPoolConfiguration getConnectionPoolConfiguration() {
        return new DefaultHttpClientConfiguration.DefaultConnectionPoolConfiguration();
    }

    private void configure() {
        setFollowRedirects(false);

        if (isDebugMode()) {
            increaseTimeouts(Duration.of(1, ChronoUnit.HOURS));
        } else {
            // in case of CI we need to increase the timeouts when Localstack is being pulled
            increaseTimeouts(Duration.of(1, ChronoUnit.MINUTES));
        }
    }

    private void increaseTimeouts(Duration timeout) {
        setConnectTimeout(timeout);
        setReadTimeout(timeout);
        setShutdownTimeout(timeout);
    }

    private boolean isDebugMode() {
        return getRuntimeMXBean().getInputArguments().stream().anyMatch(arg -> arg.contains("-agentlib:jdwp"));
    }

}
