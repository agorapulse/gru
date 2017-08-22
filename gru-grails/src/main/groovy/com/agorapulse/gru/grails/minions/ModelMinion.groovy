package com.agorapulse.gru.grails.minions

import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.grails.Grails
import com.agorapulse.gru.minions.AbstractMinion

/**
 * Minion responsible for verifying model returned from the controller action.
 */
class ModelMinion extends AbstractMinion<Grails> {

    final int index = MODEL_MINION_INDEX

    Object model

    ModelMinion() {
        super(Grails)
    }

    @Override
    void doVerify(Grails grails, Squad squad, GruContext context) throws AssertionError {
        if (model != null) {
            assert context.result == model
        }
    }
}
