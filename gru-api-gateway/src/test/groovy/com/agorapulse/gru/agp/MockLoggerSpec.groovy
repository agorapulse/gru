package com.agorapulse.gru.agp

import spock.lang.Specification

/**
 * Tests for mock logger.
 */
class MockLoggerSpec extends Specification {

    void 'mock logger logs to System.err'() {
        given:
            PrintStream old = System.err
            ByteArrayOutputStream baos = new ByteArrayOutputStream()
            PrintStream mock = new PrintStream(baos)
            System.err = mock
        when:
            MockLogger logger = new MockLogger()
            logger.log('foo')
            logger.log('bar'.bytes)
        then:
            baos.toString() == 'foo\nbar\n'
        cleanup:
            System.err = old
    }

}
