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
