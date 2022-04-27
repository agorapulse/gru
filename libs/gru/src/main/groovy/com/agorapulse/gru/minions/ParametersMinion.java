package com.agorapulse.gru.minions;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Minion responsible for URL parameters.
 */
public class ParametersMinion extends AbstractMinion<Client> {

    private final Map<String, Object> parameters = new LinkedHashMap<>();

    public ParametersMinion() {
        super(Client.class);
    }

    @Override
    public GruContext doBeforeRun(final Client client, Squad squad, GruContext context) {
        if (!parameters.isEmpty()) {
            parameters.forEach((key, value) -> client.getRequest().addParameter(key, value));
        }

        return context;
    }

    public void addParameters(Map<String, Object> params) {
        parameters.putAll(params);
    }

    public void addParameter(String key, Object value) {
        parameters.put(key, value);
    }

    public final int getIndex() {
        return PARAMETERS_MINION_INDEX;
    }


}
