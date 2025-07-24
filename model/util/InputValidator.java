package model.util;

import java.util.List;

import model.core.Player;

/**
 * <h2>InputValidator</h2>
 *
 * <p>Stateless helper class that centralises all argument and state validation
 * for <em>Fatal&nbsp;Fantasy: Tactics</em>.  Each <code>requireXxx</code> method
 * throws a {@link GameException} when the pre-condition fails, allowing
 * controllers to enforce the spec’s error-handling contract uniformly.</p>
 *
 * <p>The class is:</p>
 * <ul>
 *   <li><b>Thread-safe</b> – no mutable state.</li>
 *   <li><b>Non-instantiable</b> – private constructor throws
 *       {@link AssertionError}.</li>
 * </ul>
 *
 */
public final class InputValidator {

    /* ------------------------------------------------------------- */
    /* Construction                                                  */
    /* ------------------------------------------------------------- */

    /** Suppress default constructor for non-instantiability. */
    private InputValidator() {
        throw new AssertionError("Utility class do not instantiate.");
    }

    /* ------------------------------------------------------------- */
    /* Public static API                                             */
    /* ------------------------------------------------------------- */

    /**
     * Ensures {@code obj} is not {@code null}.
     *
     * @param obj       value to test
     * @param paramName human-readable parameter name (for error message)
     * @throws GameException if {@code obj} is {@code null}
     */
    public static void requireNonNull(Object obj, String paramName)
            throws GameException {
        if (obj == null) {
            throw new GameException(paramName + " must not be null");
        }
    }

    /**
     * Ensures {@code text} is neither {@code null} nor blank.
     *
     * @param text      string to test
     * @param paramName human-readable parameter name
     * @throws GameException if {@code text} is {@code null} or blank
     */
    public static void requireNonBlank(String text, String paramName)
            throws GameException {
        if (text == null || text.isBlank()) {
            throw new GameException(paramName + " must not be blank");
        }
    }

    /**
     * Ensures {@code value} is strictly &gt; 0.
     *
     * @param value     integer to test
     * @param paramName human-readable parameter name
     * @throws GameException if {@code value} ≤ 0
     */
    public static void requirePositive(int value, String paramName)
            throws GameException {
        if (value <= 0) {
            throw new GameException(paramName + " must be positive");
        }
    }


    /**
    * Ensures an integer is non-negative (≥ 0).
    *
    * @param value      the number to validate
    * @param fieldName  logical field name for error messaging
    * @throws GameException if {@code value} < 0
    */
    public static void requirePositiveOrZero(int value, String fieldName)
            throws GameException {
        if (value < 0) {
            throw new GameException(fieldName + " must be ≥ 0 (was " + value + ')');
        }
    }

    /**
     * Ensures {@code value} lies in the inclusive range
     * {@code [min, max]}.
     *
     * @param value     integer to test
     * @param min       lower bound (inclusive)
     * @param max       upper bound (inclusive)
     * @param paramName human-readable parameter name
     * @throws GameException if {@code value} &lt; min or &gt; max
     */
    public static void requireRange(int value, int min, int max,
                                    String paramName) throws GameException {
        if (value < min || value > max) {
            throw new GameException(paramName + " must be between "
                                    + min + " and " + max + " (inclusive)");
        }
    }


    public static void requireSize(Object[] array, int expectedSize, String message) throws GameException {
    if (array.length != expectedSize) {
        throw new GameException(message);
    }
}

/**
 * Ensures an array has an exact size.
 *
 * @param array         the array to check
 * @param expectedSize  the required size of the array
 * @param message       the exception message to use on failure
 * @throws GameException if the array's length is not equal to expectedSize
 */
public static void requireSize(Object[] array, int minSize, int maxSize, String message) throws GameException {
    if (array.length < minSize || array.length > maxSize) {
        throw new GameException(message);
    }
}

/**
 * Ensures a List has an exact size.
 *
 * @param list         the List to check
 * @param expectedSize the required size of the list
 * @param message      the exception message to use on failure
 * @throws GameException if the list's size is not equal to expectedSize
 */
public static void requireSize(List<?> list, int expectedSize, String message) throws GameException {
    if (list.size() != expectedSize) {
        throw new GameException(message);
    }
}

 public static void requireNonEmpty(String input, String fieldName) throws IllegalArgumentException {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
    }

    // Method to ensure a name is unique in your list (you can modify this as needed)
    public static void requireUniqueName(String input, List<Player> existingPlayers) throws IllegalArgumentException {
        if (existingPlayers.stream().anyMatch(p -> p.getName().equalsIgnoreCase(input))) {
            throw new IllegalArgumentException("The name " + input + " is already taken.");
        }
    }
}
