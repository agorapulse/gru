package com.agorapulse.gru.minions;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;

/**
 * Abstract class which stubs every method except Minion#getOrder() so subclasses may only implement the ones they are
 * interested in.
 * @param <C> type of the client
 */
public abstract class AbstractMinion<C extends Client> implements Minion {

    private final Class<C> clientType;

    protected AbstractMinion(Class<C> clientType) {
        this.clientType = clientType;
    }

    @Override
    public final GruContext beforeRun(Client client, Squad squad, GruContext context) {
        try {
            C c = clientType.cast(client);
            return doBeforeRun(c, squad, context);
        } catch (ClassCastException e) {
            return context.withError(e);
        }
    }

    @Override
    public final GruContext afterRun(Client client, Squad squad, GruContext context) {
        try {
            C c = clientType.cast(client);
            return doAfterRun(c, squad, context);
        } catch (ClassCastException e) {
            return context.withError(e);
        }
    }

    @Override
    public final void verify(Client client, Squad squad, GruContext context) throws Throwable {
        try {
            C c = clientType.cast(client);
            doVerify(c, squad, context);
        } catch (ClassCastException e) {
            throw new AssertionError(e);
        }

    }

    protected GruContext doBeforeRun(C client, Squad squad, GruContext context) {
        return context;
    }

    protected GruContext doAfterRun(C client, Squad squad, GruContext context) {
        return context;
    }

    protected void doVerify(C client, Squad squad, GruContext context) throws Throwable {}

}
