package model.battle;

import model.core.Ability;
import model.core.Character;
import model.util.GameException;
import model.util.InputValidator;
import model.util.StatusEffectFactory;

/**
 * Represents a battle move that executes a specific {@link Ability}.
 * This class is used in the battle system to apply ability effects
 * such as damage, healing, or status effects during a character's turn.
 */
public final class AbilityMove implements Move {

    /**
     * The ability to be executed by this move.
     */
    private final Ability ability;

    /**
     * Constructs an AbilityMove using the given ability.
     *
     * @param ability the ability this move will execute
     * @throws GameException if the ability is null
     */
    public AbilityMove(Ability ability) throws GameException {
        InputValidator.requireNonNull(ability, "AbilityMove.ability");
        this.ability = ability;
    }

    /**
     * Executes the move by applying the ability effect to the target.
     *
     * @param user   the character using the ability
     * @param target the target character affected by the ability
     * @param log    the combat log to record the action
     * @throws GameException if the ability effect is unhandled or invalid
     */
    @Override
    public void execute(Character user, Character target, CombatLog log) throws GameException {
        log.addEntry(user.getName() + " uses " + ability.getName() + "!");

        // Modular and extensible effect processing
        switch (ability.getAbilityEffectType()) {
            case DAMAGE -> target.takeDamage(ability.getEffectValue());
            case HEAL -> user.heal(ability.getEffectValue());
            case APPLY_STATUS -> {
                // Create the correct status effect using the factory
                var statusType = ability.getStatusEffectApplied();
                target.addStatusEffect(StatusEffectFactory.create(statusType));
            }
            default -> throw new GameException("Unhandled ability effect: " + ability.getAbilityEffectType());
        }
    }

    /**
     * Gets the name of the ability.
     *
     * @return the ability name
     */
    @Override
    public String getName() {
        return ability.getName();
    }

    /**
     * Gets the description of the ability.
     *
     * @return the ability description
     */
    @Override
    public String getDescription() {
        return ability.getDescription();
    }

    /**
     * Gets the EP cost of using the ability.
     *
     * @return the EP cost
     */
    @Override
    public int getEpCost() {
        return ability.getEpCost();
    }

    /**
     * Gets the underlying {@link Ability} associated with this move.
     *
     * @return the ability object
     */
    public Ability getAbility() {
        return ability;
    }
}
