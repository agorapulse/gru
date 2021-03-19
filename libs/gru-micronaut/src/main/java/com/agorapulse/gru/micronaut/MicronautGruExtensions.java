package com.agorapulse.gru.micronaut;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import io.micronaut.context.ApplicationContext;
import space.jasan.support.groovy.closure.ConsumerWithDelegate;

public class MicronautGruExtensions {

    public static Micronaut.MicronautApplicationBuilder then(
        Micronaut.MicronautApplicationBuilder self,
        @DelegatesTo(value = ApplicationContext.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "io.micronaut.context.ApplicationContext")
            Closure<ApplicationContext> configuration
    ) {
        return self.doWithContext(ConsumerWithDelegate.create(configuration));
    }

}
