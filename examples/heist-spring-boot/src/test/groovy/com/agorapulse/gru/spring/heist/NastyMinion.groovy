package com.agorapulse.gru.spring.heist

import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.minions.AbstractMinion
import com.agorapulse.gru.spring.Spring

class NastyMinion extends AbstractMinion<Spring> {

    final int index = INTERCEPTORS_MINION_INDEX

    NastyMinion() {
        super(Spring)
    }

    @Override
    protected GruContext doAfterRun(Spring client, Squad squad, GruContext context) {
        return context.withResult('You haven\'t expected this, have you?')
    }

}
