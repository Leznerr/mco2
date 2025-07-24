package model.util;

import model.util.effects.PoisonEffect;
import model.util.effects.StunEffect;

/**
 * Factory class responsible for creating concrete {@link StatusEffect}
 * instances based on their {@link StatusEffectType}.
 *
 * <p>Encapsulates creation logic to ensure clean, modular, and scalable
 * handling of status effects in accordance with the MVC architecture.</p>
 *
 * <h3>Design Highlights:</h3>
 * <ul>
 *   <li><strong>Polymorphism:</strong> Returns concrete implementations as {@link StatusEffect} references.</li>
 *   <li><strong>Encapsulation:</strong> All instantiation logic centralized here.</li>
 *   <li><strong>Error Handling:</strong> Throws {@link GameException} on unsupported or null types.</li>
 *   <li><strong>Scalability:</strong> Supports easy addition of new effects via switch expansion.</li>
 * </ul>
 *
 * @author Group 17
 * @version 1.0
 */
public final class StatusEffectFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private StatusEffectFactory() {
        throw new UnsupportedOperationException("StatusEffectFactory cannot be instantiated.");
    }

    /**
     * Creates a concrete {@link StatusEffect} instance corresponding to the specified {@link StatusEffectType}.
     *
     * @param type non-null {@link StatusEffectType} specifying which effect to create
     * @return a new, immutable {@link StatusEffect} instance
     * @throws GameException if {@code type} is null or unsupported
     */
    public static StatusEffect create(StatusEffectType type) throws GameException {
        InputValidator.requireNonNull(type, "StatusEffectType");

        return switch (type) {
            case POISONED -> new PoisonEffect();
            case STUNNED -> new StunEffect();
            // Extend here as new StatusEffectTypes are added
            default -> throw new GameException("Unsupported StatusEffectType: " + type);
        };
    }
}
