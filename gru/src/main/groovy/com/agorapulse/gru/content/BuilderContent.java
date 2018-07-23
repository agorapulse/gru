package com.agorapulse.gru.content;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.Content;
import com.agorapulse.gru.minions.Minion;
import groovy.json.JsonBuilder;
import groovy.lang.Closure;
import groovy.xml.MarkupBuilder;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.io.*;

/**
 * Builds the content using either JsonBuilder or MarkupBuilder based on the name of the minion.
 */
public class BuilderContent implements Content {

    public static Content create(Closure definition) {
        return new BuilderContent(definition);
    }

    private final Closure definition;

    private BuilderContent(Closure definition) {
        this.definition = definition;
    }

    @Override
    public InputStream load(Minion minion, Client client) {
        if (minion != null) {
            String minionName = minion.getClass().getName().toLowerCase();
            if (minionName.contains("json")) {
                return loadJson();
            } else if(minionName.contains("html") || minionName.contains("xml")) {
                return loadXml();
            }
        }
        return loadJson();
    }

    @Override
    public boolean isSaveSupported() {
        return false;
    }

    @Override
    public void save(Client client, InputStream stream) {
        throw new UnsupportedOperationException("Saving is not supported for StringContent");
    }

    private InputStream loadJson() {
        JsonBuilder builder = new JsonBuilder();
        builder.call(definition);
        return new ByteArrayInputStream(builder.toString().getBytes());
    }

    private InputStream loadXml() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        MarkupBuilder builder = new MarkupBuilder(writer);
        DefaultGroovyMethods.with(builder, definition);
        return new ByteArrayInputStream(stringWriter.toString().getBytes());
    }

}
