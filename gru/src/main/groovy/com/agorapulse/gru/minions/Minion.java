package com.agorapulse.gru.minions;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.GruContext;
import com.agorapulse.gru.Squad;

import java.util.Comparator;

/**
 * Minions helps Gru with setting up and verifying the tests.
 */
public interface Minion {

    // tag::constants[]
    int INITIALIZATION_MINION_INDEX = -10000;
    int HTTP_MINION_INDEX = 0;
    int URL_MAPPINGS_MINION_INDEX = 10000;
    int PARAMETERS_MINION_INDEX = 20000;
    int JSON_MINION_INDEX = 30000;
    int INTERCEPTORS_MINION_INDEX = 50000;
    int MODEL_MINION_INDEX = 60000;
    // end::constants[]

    /**
     * Comparator to compare minions by their index.
     */
    Comparator<Minion> COMPARATOR = new Comparator<Minion>() {
        @Override
        public int compare(Minion o1, Minion o2) {
            return Integer.compare(o1.getIndex(), o2.getIndex());
        }
    };

    // tag::methods[]
    /**
     * @return index of the minion to so they they handle the test in given order
     */
    int getIndex();

    /**
     * Prepares the execution of controller action.
     * @param client controller unit test
     * @param squad minion's colleagues
     * @param context context of execution, in this phase you should only add errors if some conditions are not met
     * @return new context holding any exception which happens during the preparation or the initial context from parameter
     */
    GruContext beforeRun(Client client, Squad squad, GruContext context);

    /**
     * Modifies the result of the execution of controller action.
     *
     * Minions handle the result in reversed order.
     *
     * @param client controller unit test
     * @param squad minion's colleagues
     * @param context context of execution, in this phase can modify the result or add exception
     * @return modified context or the initial one from the parameter
     */
    GruContext afterRun(Client client, Squad squad, GruContext context);

    /**
     * Verifies the response after execution of the controller action.
     *
     * @param client controller unit test
     * @param squad minion's colleagues
     * @param context context of the execution, in this phase you can't modify the context, you can just throw any {@link Throwable} based on the current context state
     * @throws Throwable if any of the conditions are not met
     */
    void verify(Client client, Squad squad, GruContext context) throws Throwable;
    // end::methods[]

}
