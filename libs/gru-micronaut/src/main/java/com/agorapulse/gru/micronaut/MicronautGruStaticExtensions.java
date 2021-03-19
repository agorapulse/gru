package com.agorapulse.gru.micronaut;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import io.micronaut.context.ApplicationContextBuilder;
import space.jasan.support.groovy.closure.ConsumerWithDelegate;

public class MicronautGruStaticExtensions {

    @SuppressWarnings("unused")
    public static Micronaut.MicronautApplicationBuilder build(
        Micronaut self,
        Object unitTest,
        @DelegatesTo(value = ApplicationContextBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "io.micronaut.context.ApplicationContextBuilder")
            Closure<ApplicationContextBuilder> configuration
    ) {
        return Micronaut.build(unitTest).doWithContextBuilder(ConsumerWithDelegate.create(configuration));
    }

}
