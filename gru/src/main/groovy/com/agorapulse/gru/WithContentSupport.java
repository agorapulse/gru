package com.agorapulse.gru;

import groovy.lang.Closure;

/**
 * Creates content from inline string.
 */
public interface WithContentSupport {

    Content inline(String string);
    Content build(Closure closure);

}
