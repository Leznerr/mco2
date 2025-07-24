package model.util;

/*
 * A project-wide unchecked exception used to signal any domain-level
 * error (invalid state, failed validation, persistence issues, etc.).
 * Centralising on a single subtype simplifies controller code and
 * supports uniform error handling in the UI layer.
 *
 * <p><b>Immutability :</b> instances are effectively immutable;
 * message and cause are set once via the constructor.</p>
 *
 */
public final class GameException extends RuntimeException {

    /**
     * Creates a {@code GameException} with a human-readable message.
     *
     * @param message non-blank detail describing the failure
     * @throws IllegalArgumentException if {@code message} is {@code null}
     *                                  or blank
     */
    public GameException(final String message) {
        super(validate(message));
    }

    /**
     * Creates a {@code GameException} with a message and root cause.
     *
     * @param message non-blank description
     * @param cause   underlying throwable (may be {@code null})
     * @throws IllegalArgumentException if {@code message} is {@code null}
     *                                  or blank
     */
    public GameException(final String message, final Throwable cause) {
        super(validate(message), cause);
    }

    /* ------------------------------------------------------------------ */
    /* Private helpers                                                    */
    /* ------------------------------------------------------------------ */

    /** Ensures a user-facing message is present. */
    private static String validate(final String msg) {
        if (msg == null || msg.isBlank()) {
            throw new IllegalArgumentException("GameException message must not be blank");
        }
        return msg;
    }
}
