package com.agorapulse.gru.minions;

public interface Command<M extends  Minion> {

    void execute(M minion);
}
