package com.agorapulse.gru.content;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.Content;
import com.agorapulse.gru.minions.AbstractContentMinion;
import org.codehaus.groovy.runtime.IOGroovyMethods;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileContent implements Content {

    private final String fileName;

    public static Content create(String fileName) {
        return new FileContent(fileName);
    }

    private FileContent(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public InputStream load(Client client) {
        return client.loadFixture(fileName);
    }

    @Override
    public boolean isSaveSupported() {
        return true;
    }

    @Override
    public void save(Client client, InputStream stream) throws IOException {
        String path = client.getFixtureLocation(fileName);
        String testResourcesFromSystem = System.getProperty(AbstractContentMinion.TEST_RESOURCES_FOLDER_PROPERTY_NAME);
        File file = new File(testResourcesFromSystem != null ? testResourcesFromSystem : "src/test/resources", path);
        file.getParentFile().mkdirs();
        try (FileOutputStream out = new FileOutputStream(file)) {
            IOGroovyMethods.setBytes(out, IOGroovyMethods.getBytes(stream));
        }
    }

    @Override
    public String toString() {
        return "file " + fileName;
    }
}
