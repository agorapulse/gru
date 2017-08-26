package com.agorapulse.gru.grails.minions

import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.grails.Grails
import com.agorapulse.gru.minions.AbstractMinion

/**
 * Minion responsible for verifying model returned from the controller action.
 */
class ModelMinion extends AbstractMinion<Grails> {                                      // <1>

    final int index = MODEL_MINION_INDEX                                                // <2>

    Object model                                                                        // <3>

    ModelMinion() {
        super(Grails)                                                                   // <4>
    }

    @Override
    @SuppressWarnings('Instanceof')
    void doVerify(Grails grails, Squad squad, GruContext context) throws Throwable {    // <5>
        if (model instanceof Map && context.result instanceof Map) {
            model.each { key, value ->
                assert context.result[key] == value
            }
        } else if (model != null) {
            assert context.result == model
        }
    }
}