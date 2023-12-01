package com.agorapulse.gru.micronaut.http;

import io.micronaut.http.client.DefaultHttpClientConfiguration;
import io.micronaut.http.client.HttpClientConfiguration;
import io.micronaut.runtime.ApplicationConfiguration;
import jakarta.inject.Named;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static java.lang.management.ManagementFactory.getRuntimeMXBean;

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
            increaseTimeouts();
        }

    }

    private void increaseTimeouts() {
        setConnectTimeout(Duration.of(1, ChronoUnit.HOURS));
        setReadTimeout(Duration.of(1, ChronoUnit.HOURS));
        setShutdownTimeout(Duration.of(1, ChronoUnit.HOURS));
    }

    private boolean isDebugMode() {
        return getRuntimeMXBean().getInputArguments().stream().anyMatch(arg -> arg.contains("-agentlib:jdwp"));
    }

}
