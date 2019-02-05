package com.agorapulse.gru.http

import com.agorapulse.gru.Client
import com.agorapulse.gru.Gru
import groovy.transform.CompileStatic

@CompileStatic
class HttpExtensions {

    /**
     * Prepare gru with given base uri
     * @param gru self
     * @param aBaseUri a base uri
     * @return self with base uri configured
     */
    static <T extends Client> Gru<T> prepare(final Gru<T> gru, final String aBaseUri) {
        gru.prepare {
            baseUri aBaseUri
        }
    }

}
