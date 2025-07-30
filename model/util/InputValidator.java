package model.util;

import java.util.List;
import java.util.Collection;
import java.util.Arrays;

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


    /**
     * Validates that the provided {@code size} matches the expected size.
     *
     * @param size         the actual size to validate
     * @param expectedSize the exact size expected
     * @param message      the error message to include in the exception if validation fails
     * @throws GameException if {@code size} is not equal to {@code expectedSize}
     */
    public static void requireSize(int size, int expectedSize,
                                   String message) throws GameException {
        if (size != expectedSize) {
            throw new GameException(message);
        }
    }

    /**
     * Validates that the provided {@code size} falls within the inclusive range {@code [minSize, maxSize]}.
     *
     * @param size     the actual size to validate
     * @param minSize  the minimum allowed value (inclusive)
     * @param maxSize  the maximum allowed value (inclusive)
     * @param message  the error message to include in the exception if validation fails
     * @throws GameException if {@code size} is less than {@code minSize} or greater than {@code maxSize}
     */
    public static void requireSize(int size, int minSize, int maxSize,
                                   String message) throws GameException {
        if (size < minSize || size > maxSize) {
            throw new GameException(message);
        }
    }

    /**
     * Ensures a string is not {@code null} or empty after trimming.
     *
     * @param input     the text to validate
     * @param fieldName logical name for error messages
     * @throws GameException if {@code input} is {@code null} or blank
     */
    public static void requireNonEmpty(String input, String fieldName)
            throws GameException {
        if (input == null || input.trim().isEmpty()) {
            throw new GameException(fieldName + " cannot be empty.");
        }
    }

    // Method to ensure a name is unique in your list (you can modify this as needed)
    /**
     * Ensures a player name is unique within the provided list.
     *
     * @param input           proposed name
     * @param existingPlayers list of players to check against
     * @throws GameException if {@code input} already exists in the list
     */
    public static void requireUniqueName(String input, List<Player> existingPlayers)
            throws GameException {
        if (existingPlayers.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(input))) {
            throw new GameException("The name " + input + " is already taken.");
        }
    }

    /**
     * Ensures all elements in the collection are distinct.
     *
     * @param items   collection to validate (non-null)
     * @param message error message if duplicates are found
     * @throws GameException if the collection contains duplicate elements
     */
    public static void requireDistinct(Collection<?> items, String message)
            throws GameException {
        requireNonNull(items, "items");
        long distinctCount = items.stream().distinct().count();
        if (distinctCount != items.size()) {
            throw new GameException(message);
        }
    }

    /**
     * Ensures all values in the provided array are distinct.
     *
     * @param values  array to validate (non-null)
     * @param message error message if duplicates are found
     * @throws GameException if the array contains duplicate elements
     */
    public static void requireDistinct(Object[] values, String message)
            throws GameException {
        requireNonNull(values, "values");
        requireDistinct(Arrays.asList(values), message);
    }
}
