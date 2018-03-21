package com.agorapulse.gru;

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
    InputStream load(Client client);

    /**
     * @return true if saving is supported for this content implementation.
     */
    boolean isSaveSupported();

    void save(Client client, InputStream stream) throws IOException;

}
