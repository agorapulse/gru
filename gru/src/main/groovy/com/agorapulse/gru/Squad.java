package com.agorapulse.gru;

import com.agorapulse.gru.exception.GroovyAssertAwareMultipleFailureException;
import com.agorapulse.gru.minions.Command;
import com.agorapulse.gru.minions.Minion;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Squad is pool of minions engaged in a test.
 */
public class Squad {

    private ClassToInstanceMap<Minion> minions = MutableClassToInstanceMap.create();
    private Set<Minion> sorted = new TreeSet<>(Minion.COMPARATOR);

    /**
     * Adds a minion to the squad.
     * @param minion minion who should join the squad
     */
    public <M extends Minion> void add(M minion) {
        minions.put(minion.getClass(), minion);

        Class sc = minion.getClass().getSuperclass();
        while (sc != null && !Modifier.isAbstract(sc.getModifiers()) && !sc.equals(Minion.class)
        ) {
            minions.putInstance(sc, minion);
            sc = sc.getSuperclass();
        }

        sorted.add(minion);
    }

    /**
     * Command minion of given type.
     *
     * If the minion is not yet present in the squad it is instantiated using default constructor.
     * @param minionType type of the minion being commanded
     * @param command closure executed within context of selected minion
     */
    @SuppressWarnings("unchecked")
    public <M extends Minion> void command(Class<M> minionType, @DelegatesTo(type = "M", strategy = Closure.DELEGATE_FIRST) Closure command) {
        DefaultGroovyMethods.with(findOrCreateMinionByType(minionType), command);
    }

    /**
     * Command minion of given type.
     *
     * If the minion is not yet present in the squad it is instantiated using default constructor.
     * @param minionType type of the minion being commanded
     * @param command closure executed within context of selected minion
     */
    public <M extends Minion> void command(Class<M> minionType, Command<M> command) {
        command.execute(findOrCreateMinionByType(minionType));
    }

    private <M extends Minion> M findOrCreateMinionByType(Class<M> minionType) {
        M minion = minions.getInstance(minionType);

        if (minion == null) {
            try {
                minion = minionType.newInstance();
                add(minion);
            } catch (Exception e) {
                throw new IllegalStateException("Unable to crete new instance of " + minionType + ". Please, provide public default constructor for the class or add the minion to the squad manually using Gru.engage(minion) method.", e);
            }
        }
        return minion;
    }

    /**
     * Asks minion of given type for something.
     * @param minionType type of the minion being asked
     * @param query closure executed within context of selected minion which returns the result of this method
     * @return result returned from the query closure or null if minion of given type is not present in the squad
     */
    public <T, M extends Minion> T ask(Class<M> minionType, @DelegatesTo(type = "M", strategy = Closure.DELEGATE_FIRST) Closure<T> query) {
        M minion = minions.getInstance(minionType);

        if (minion == null) {
            return null;
        }

        return DefaultGroovyMethods.with(minion, query);
    }

    GruContext beforeRun(Client client, GruContext context) {
        GruContext ctx = context;
        for (Minion minion: sorted) {
            ctx = minion.beforeRun(client, this, ctx);
        }
        return ctx;
    }

    GruContext afterRun(Client client, GruContext context) {
        GruContext ctx = context;
        for (Minion minion: sorted) {
            ctx = minion.afterRun(client, this, ctx);
        }
        return ctx;
    }

    void verify(Client client, GruContext context) throws Throwable {
        List<Throwable> errors = new ArrayList<>();

        for (Minion minion : sorted) {
            try {
                minion.verify(client, this, context);
            } catch (Throwable t) {
                errors.add(t);
            }
        }

        if (errors.size() == 1) {
            throw errors.get(0);
        }

        if (errors.size() > 0) {
            throw new GroovyAssertAwareMultipleFailureException(errors);
        }
    }
}
