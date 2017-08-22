package com.agorapulse.gru.exception;

import org.junit.runners.model.MultipleFailureException;

import java.util.List;

public class GroovyAssertAwareMultipleFailureException extends MultipleFailureException {

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(String.format("There were %d errors:", getFailures().size()));
        for (Throwable e : getFailures()) {
            sb.append(String.format("\n  %s(%s)", e.getClass().getName(), e.toString()));
        }

        return sb.toString();
    }

    public GroovyAssertAwareMultipleFailureException(List<Throwable> errors) {
        super(errors);
    }

}
