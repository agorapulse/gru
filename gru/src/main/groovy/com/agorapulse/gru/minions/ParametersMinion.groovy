package com.agorapulse.gru.minions

import com.agorapulse.gru.Client
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import groovy.transform.CompileStatic

/**
 * Minion responsible for URL parameters.
 */
@CompileStatic
class ParametersMinion extends AbstractMinion<Client>  {

    final int index = PARAMETERS_MINION_INDEX

    Map<String, Object> parameters = [:]

    ParametersMinion() {
        super(Client)
    }

    @Override
    GruContext doBeforeRun(Client client, Squad squad, GruContext context) {
        if (parameters) {
            parameters.each { String key, Object value ->
                client.request.addParameter(key, value)
            }
        }
        context
    }

}
