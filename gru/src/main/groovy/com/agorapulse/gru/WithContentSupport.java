package com.agorapulse.gru;

import com.agorapulse.gru.content.StringContent;

/**
 * Creates content from inline string.
 */
public interface WithContentSupport {

    default Content inline(String string) {
        return StringContent.create(string);
    }

}
