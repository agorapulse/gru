package com.agorapulse.gru.grails.minions

import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.grails.Grails
import com.agorapulse.gru.minions.AbstractMinion

/**
 * Minion responsible for verifying model returned from the controller action.
 */
class ForwardMinion extends AbstractMinion<Grails> {

    final int index = HTTP_MINION_INDEX + 1000

    String forwardedUri

    ForwardMinion() {
        super(Grails)
    }

    @Override
    void doVerify(Grails grails, Squad squad, GruContext context) throws AssertionError {
        if (forwardedUri) {
            assert forwardedUri == grails.unitTest.response.forwardedUrl
        }
    }
}
