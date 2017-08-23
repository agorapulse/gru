package com.agorapulse.gru.grails.minions;

import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;
import com.agorapulse.gru.grails.Grails;
import com.agorapulse.gru.minions.AbstractMinion;

/**
 * Initialization controller ensures that Controller under test is initialized.
 */
public class ControllerInitializationMinion extends AbstractMinion<Grails> {

    public ControllerInitializationMinion() {
        super(Grails.class);
    }

    @Override
    public int getIndex() {
        return INITIALIZATION_MINION_INDEX;
    }

    @Override
    protected GruContext doBeforeRun(Grails client, Squad squad, GruContext context) {
        client.getUnitTest().getController();
        return super.doBeforeRun(client, squad, context);
    }
}
