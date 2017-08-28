package com.agorapulse.gru;

import com.agorapulse.gru.minions.Minion;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractClient implements Client {

    protected AbstractClient(Object unitTest) {
        this.unitTest = unitTest;
    }

    @Override
    public final String getFixtureLocation(String fileName) {
        String suffix = unitTest.getClass().getSimpleName() + "/" + fileName;
        Package pkg = unitTest.getClass().getPackage();
        if (pkg == null) {
            return suffix;
        }
        return pkg.getName().replaceAll("\\.", File.separator) + File.separator + suffix;
    }

    public final InputStream loadFixture(String fileName) {
        return unitTest.getClass().getResourceAsStream(unitTest.getClass().getSimpleName() + "/" + fileName);
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
}
