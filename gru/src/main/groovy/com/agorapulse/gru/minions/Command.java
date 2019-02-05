package com.agorapulse.gru.minions;

import groovy.lang.Closure;
import space.jasan.support.groovy.closure.ConsumerWithDelegate;

public interface Command<M extends  Minion> {

    Command NOOP = minion -> { };

    static <M extends Minion> Command<M> create(Closure<M> closure) {
        return (m) -> ConsumerWithDelegate.create(closure).accept(m);
    }

    void execute(M minion);

}
