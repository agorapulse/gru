package com.agorapulse.gru.content;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.Content;
import com.agorapulse.gru.minions.Minion;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class StringContent implements Content {

    public static Content create(String content) {
        return new StringContent(content);
    }

    private final String content;

    private StringContent(String content) {
        this.content = content;
    }

    @Override
    public InputStream load(Minion minion, Client client) {
        return new ByteArrayInputStream(content.getBytes());
    }

    @Override
    public boolean isSaveSupported() {
        return false;
    }

    @Override
    public void save(Client client, InputStream stream) {
        throw new UnsupportedOperationException("Saving is not supported for StringContent");
    }
}
