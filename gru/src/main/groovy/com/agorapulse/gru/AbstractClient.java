package com.agorapulse.gru;

import com.agorapulse.gru.minions.Minion;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractClient implements Client {

    protected AbstractClient(String baseUrl, Object unitTest) {
        this.baseUrl = baseUrl;
        this.unitTest = unitTest;
    }

    @Override
    public final String getFixtureLocation(String fileName) {
        return unitTest.getClass().getPackage().getName().replaceAll("\\.", File.separator)
        + File.separator + unitTest.getClass().getSimpleName() + "/" + fileName;
    }

    public final InputStream loadFixture(String fileName) {
        return unitTest.getClass().getResourceAsStream(unitTest.getClass().getSimpleName() + "/" + fileName);
    }

    @Override
    public String getCurrentDescription() {
        return getRequest().getMethod() + " on " + baseUrl + getRequest().getUri();
    }

    @Override
    public List<Minion> getInitialSquad() {
        return new ArrayList<Minion>();
    }

    @Override
    public String toString() {
        return getCurrentDescription();
    }

    protected final String baseUrl;
    protected final Object unitTest;
}
