package com.agorapulse.gru.minions;

import com.agorapulse.gru.Client;

import java.util.Objects;

/**
 * Minion responsible for JSON requests and responses.
 */
public class TextMinion extends AbstractContentMinion<Client> {
    public TextMinion() {
        super(Client.class);
    }

    @Override
    protected String normalize(String input) {
        return input.trim();
    }

    protected void similar(String actual, String expected) throws AssertionError {
        if (!Objects.equals(actual, expected)) {
            throw new AssertionError("The texts are not equal:\n\nExpected:\n" + expected + "\n\nActual:\n" + actual);
        };
    }

    public final int getIndex() {
        return TEXT_MINION_INDEX;
    }

}
