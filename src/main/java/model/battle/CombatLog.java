package model.battle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.util.GameException;
import model.util.InputValidator;

/**
 * Stores an ordered list of textual battle events.
 *
 * <p>The log is cleared at the start of each new {@code Battle}.  UI layers
 * must treat the list returned by {@link #getLogEntries()} as <em>read-only</em>;
 * mutation is only allowed through {@link #addEntry(String)} and
 * {@link #clearLog()} to preserve consistency.</p>
 */
public final class CombatLog {

    /** Internal backing list; never exposed directly. */
    private final List<String> logEntries = new ArrayList<>();

    /** Creates an empty log. */
    public CombatLog() { /* nothing to initialise */ }

    /**
     * Appends a new entry to the combat log.
     *
     * @param entry non-blank event text
     * @throws GameException if {@code entry} is null or blank
     */
    public synchronized void addEntry(String entry) throws GameException {
        InputValidator.requireNonBlank(entry, "CombatLog entry");
        logEntries.add(entry);
    }

    /**
     * Returns an unmodifiable view of the current log entries in insertion order.
     *
     * @return immutable list of log lines
     */
    public synchronized List<String> getLogEntries() {
        return Collections.unmodifiableList(new ArrayList<>(logEntries));
    }


    /**
     * Removes all entries from the log.
     */
    public synchronized void clearLog() {
        logEntries.clear();
    }
}
