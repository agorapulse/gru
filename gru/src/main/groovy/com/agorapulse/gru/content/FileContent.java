package com.agorapulse.gru.content;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.Content;
import com.agorapulse.testing.fixt.Fixt;

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
        Fixt.create(client.getUnitTest().getClass()).writeStream(fileName, stream);
    }

    @Override
    public String toString() {
        return "file " + fileName;
    }
}
