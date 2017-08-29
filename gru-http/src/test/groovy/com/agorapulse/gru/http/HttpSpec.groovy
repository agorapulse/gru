package com.agorapulse.gru.http

import com.agorapulse.gru.Gru
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.minions.AbstractMinion
import spock.lang.Specification

/**
 * Tests for Http.
 */
class HttpSpec extends Specification {

    void 'response is not set before the action is executed'() {
        when:
            Gru.equip(Http.steal(this)).engage(new VioletMinion()).test {
                get '/foo/bar'
            }.verify()
        then:
            IllegalStateException error = thrown(IllegalStateException)
            error.message == 'Response hasn\'t been set yet'
    }

    static class VioletMinion extends AbstractMinion<Http> {

        int index = 0

        VioletMinion() {
            super(Http)
        }

        @Override
        protected GruContext doBeforeRun(Http client, Squad squad, GruContext context) {
            client.response
            context
        }
    }

}
