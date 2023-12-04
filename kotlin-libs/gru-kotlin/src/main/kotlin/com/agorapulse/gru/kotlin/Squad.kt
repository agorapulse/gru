package com.agorapulse.gru.kotlin

import com.agorapulse.gru.Squad
import com.agorapulse.gru.minions.Minion

class Squad constructor(val delegate: Squad) {

    /**
     * Adds a minion to the squad.
     * @param minion minion who should join the squad
     */
    fun <M : Minion?> add(minion: M) {
        delegate.add(minion)
    }

    /**
     * Command minion of given type.
     *
     * If the minion is not yet present in the squad it is instantiated using default constructor.
     * @param minionType type of the minion being commanded
     * @param command closure executed within context of selected minion
     */
    inline fun <reified M : Minion?> command(noinline command: M.() -> Any) {
        delegate.command(M::class.java) { command(it) }
    }

    /**
     * Asks minion of given type for something.
     * @param minionType type of the minion being asked
     * @param query function executed with the selected minion which returns the result of this method
     * @return result returned from the query closure or null if minion of given type is not present in the squad
     */
    inline fun <reified M : Minion?, T> ask(
        noinline query: M.() -> T
    ): T? {
        return delegate.ask(M::class.java) { query(it) }
    }

}
