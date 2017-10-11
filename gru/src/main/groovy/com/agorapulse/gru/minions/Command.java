package com.agorapulse.gru.minions;

public interface Command<M extends  Minion> {

    Command NOOP = new Command() { @Override public void execute(Minion minion) { } };

    void execute(M minion);
}
