package com.agorapulse.gru;

import com.agorapulse.gru.minions.Minion;
import com.agorapulse.testing.fixt.Fixt;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractClient implements Client {

    protected AbstractClient(Object unitTest) {
        this.unitTest = unitTest;
        this.fixt = Fixt.create(unitTest.getClass());
    }

    public final InputStream loadFixture(String fileName) {
        return fixt.readStream(fileName);
    }

    @Override
    public String getCurrentDescription() {
        StringBuilder builder = new StringBuilder(getRequest().getMethod());
        builder.append(" on ");
        if (getRequest().getBaseUri() != null) {
            builder.append(getRequest().getBaseUri());
        }
        if (getRequest().getUri() != null) {
            builder.append(getRequest().getUri());
        }
        return builder.toString();
    }

    @Override
    public List<Minion> getInitialSquad() {
        return new ArrayList<>();
    }

    @Override
    public Object getUnitTest() {
        return unitTest;
    }

    @Override
    public String toString() {
        return getCurrentDescription();
    }

    protected final Object unitTest;
    protected final Fixt fixt;
}
