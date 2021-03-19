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
