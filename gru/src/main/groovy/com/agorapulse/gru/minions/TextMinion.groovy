package com.agorapulse.gru.minions

import com.agorapulse.gru.Client
import groovy.transform.CompileStatic
import groovy.util.logging.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.DocumentType
import org.xmlunit.builder.DiffBuilder
import org.xmlunit.builder.Input
import org.xmlunit.diff.*

import javax.xml.transform.Source

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
