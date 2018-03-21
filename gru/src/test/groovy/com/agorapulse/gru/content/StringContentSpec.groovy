package com.agorapulse.gru.content

import com.agorapulse.gru.Content
import spock.lang.Specification

class StringContentSpec extends Specification {

    void 'save is not supported'() {
        given:
            Content content = StringContent.create('foo')
        expect:
            !content.saveSupported

        when:
            content.save(null, null)
        then:
            thrown(UnsupportedOperationException)

    }
}
