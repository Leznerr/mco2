package model.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Unit tests for InputValidator */
public class InputValidatorTest {

    @Test
    public void testRequireNonNullThrows() {
        assertThrows(GameException.class, () -> InputValidator.requireNonNull(null, "x"));
    }

    @Test
    public void testRequireNonBlankThrows() {
        assertThrows(GameException.class, () -> InputValidator.requireNonBlank(" ", "y"));
    }

    @Test
    public void testRequireRange() {
        assertDoesNotThrow(() -> InputValidator.requireRange(5, 0, 10, "val"));
        assertThrows(GameException.class, () -> InputValidator.requireRange(11, 0, 10, "val"));
    }
}
