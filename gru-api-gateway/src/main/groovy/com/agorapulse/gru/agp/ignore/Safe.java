package com.agorapulse.gru.agp.ignore;

import java.util.concurrent.Callable;

public class Safe {

    private Safe() { }

    public static <V> V call(Callable<V> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            if (e instanceof RuntimeException) { // NOSONAR
                throw (RuntimeException) e;
            }
            throw new IllegalStateException("Unchecked exception which should never happen was thrown", e);
        }
    }
}
