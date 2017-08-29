package com.agorapulse.gru

import com.agorapulse.gru.minions.AbstractMinion
import com.agorapulse.gru.minions.HttpMinion
import spock.lang.Specification

/**
 * Tests for Squad.
 */
class SquadSpec extends Specification {

    void 'asking minion which is not in squad returns null'() {
        expect:
            new Squad().ask(HttpMinion) { status } == null
    }

    void 'minion must be instantiable'() {
        when:
            new Squad().command(AbstractMinion) { }
        then:
            thrown(IllegalStateException)
    }

}
