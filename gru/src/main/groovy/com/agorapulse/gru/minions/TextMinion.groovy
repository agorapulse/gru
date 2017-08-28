package com.agorapulse.gru.minions

import com.agorapulse.gru.Client
import groovy.transform.CompileStatic
import groovy.util.logging.Log

/**
 * Minion responsible for JSON requests and responses.
 */
@Log @CompileStatic
class TextMinion extends AbstractContentMinion<Client> {

    final int index = TEXT_MINION_INDEX

    TextMinion() {
        super(Client)
    }

    protected void similar(String actual, String expected) throws AssertionError {
       assert actual == expected
    }
}
