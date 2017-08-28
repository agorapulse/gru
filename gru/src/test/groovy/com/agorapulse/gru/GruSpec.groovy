package com.agorapulse.gru

import org.junit.runner.Description
import org.junit.runners.model.Statement
import spock.lang.Specification

/**
 * Tests for Gru.
 */
class GruSpec extends Specification {

    @SuppressWarnings('UnnecessaryGetter')
    void 'test is verified'() {
        given:
            Description description = Description.createTestDescription(GruSpec, 'test is verified')
            Statement statement = new Statement() { @Override void evaluate() throws Throwable { } }
            Client.Request request = Mock(Client.Request)
            Client client = Mock(Client) {
                getInitialSquad() >> []
                getRequest() >> request
                run(_, _) >> GruContext.EMPTY
            }
            Gru gru = Gru.equip(client)
        when:
            gru.test {
                get '/foo/bar'
            }
            gru.apply(statement, description).evaluate()
        then:
            AssertionError e = thrown(AssertionError)
            e.message?.startsWith('Test wasn\'t verified.')

    }

}
