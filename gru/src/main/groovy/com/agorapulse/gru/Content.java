package com.agorapulse.gru;

import com.agorapulse.gru.minions.Minion;

import java.io.IOException;
import java.io.InputStream;

/**
 * Opaque content wrapper.
 */
public interface Content {

    /**
     * Loads a content.
     * @return the content represented as InputStr¨
     */
    InputStream load(Minion minion, Client client);

    /**
     * @return true if saving is supported for this content implementation.
     */
    boolean isSaveSupported();

    void save(Client client, InputStream stream) throws IOException;

}
