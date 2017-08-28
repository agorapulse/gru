package com.agorapulse.gru.minions

import com.agorapulse.gru.AbstractClient
import com.agorapulse.gru.Client
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import spock.lang.Specification

/**
 * Tests for abstract minion.
 */
class AbstractMinionSpec extends Specification {

    void 'test type checking'() {
        given:
            AbstractMinion<MyClient> minion = new AbstractMinion<MyClient>(MyClient) {
                int index
            }
            MyOtherClient mock = new MyOtherClient(this)
            Squad squad = new Squad()
        when:
            GruContext beforeRun = minion.beforeRun(mock, squad, GruContext.EMPTY)
        then:
            beforeRun.hasError(ClassCastException)
        when:
            GruContext afterRun = minion.afterRun(mock, squad, GruContext.EMPTY)
        then:
            afterRun.hasError(ClassCastException)
        when:
            minion.verify(mock, squad, GruContext.EMPTY)
        then:
            thrown(AssertionError)
    }

    private static class MyClient extends AbstractClient {

        MyClient(Object unitTest) {
            super(unitTest)
        }

        Client.Request request
        Client.Response response

        @Override
        void reset() { }

        @Override
        GruContext run(Squad squad, GruContext context) { GruContext.EMPTY }
    }

    private static class MyOtherClient extends AbstractClient {

        MyOtherClient(Object unitTest) {
            super(unitTest)
        }

        Client.Request request
        Client.Response response

        @Override
        void reset() { }

        @Override
        GruContext run(Squad squad, GruContext context) { GruContext.EMPTY }
    }

}
