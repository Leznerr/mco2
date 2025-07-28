package model.battle;

import model.core.Ability;
import model.core.Character;
import model.util.GameException;
import model.util.InputValidator;
import model.util.StatusEffectFactory;
import model.util.StatusEffectType;
import model.core.AbilityEffectType;

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
        // Spend EP upfront; if not enough EP the move fails
        int cost = ability.getEpCost();
        if (cost > 0 && !user.spendEp(cost)) {
            throw new GameException(user.getName() + " does not have enough EP to use " + ability.getName());
        }

        log.addEntry(user.getName() + " uses " + ability.getName() + "!");

        // Modular and extensible effect processing
        switch (ability.getAbilityEffectType()) {
            case DAMAGE -> {
                int dmg = ability.getEffectValue();
                if (user.getRaceType() == model.core.RaceType.ORC && user.getCurrentHp() < user.getMaxHp() * 0.3) {
                    dmg += 10;
                    log.addEntry("Orc " + user.getName() + " unleashes Rage! +10 ability damage.");
                }
                int before = target.getCurrentHp();
                target.takeDamage(dmg);
                int dealt = before - target.getCurrentHp();
                log.addEntry(target.getName() + " takes " + dealt + " damage.");

                if (ability.getName().equals("Summon Spirit Wolf")) {
                    int drain = Math.min(2, target.getCurrentEp());
                    target.spendEp(drain);
                    log.addEntry(target.getName() + " loses " + drain + " EP.");
                }
                if (ability.getName().equals("Elemental Pact")) {
                    user.heal(12);
                    log.addEntry(user.getName() + " heals 12 HP.");
                }
                if (ability.getName().equals("Greater Summoning")) {
                    user.gainEp(8);
                    log.addEntry(user.getName() + " gains 8 EP.");
                }
                if (ability.getName().equals("EMP Grenade")) {
                    int drain = Math.min(6, target.getCurrentEp());
                    target.spendEp(drain);
                    log.addEntry(target.getName() + " loses " + drain + " EP.");
                }
                if (ability.getName().equals("Overclock")) {
                    user.takeDamage(6);
                    log.addEntry(user.getName() + " suffers 6 recoil damage.");
                }
                if (ability.getName().equals("Deploy Turret")) {
                    target.addStatusEffect(StatusEffectFactory.create(StatusEffectType.MARKED));
                    log.addEntry(target.getName() + " is now MARKED.");
                }
                if (user.getRaceType() == model.core.RaceType.VAMPIRE && dealt > 0) {
                    int heal = dealt / 3;
                    if (heal > 0) {
                        user.heal(heal);
                        log.addEntry("Vampire " + user.getName() + " lifesteals " + heal + " HP!");
                    }
                }
            }
            case HEAL -> {
                user.heal(ability.getEffectValue());
                log.addEntry(user.getName() + " heals " + ability.getEffectValue() + " HP.");
            }
            case ENERGY_GAIN -> {
                user.gainEp(ability.getEffectValue());
                log.addEntry(user.getName() + " gains " + ability.getEffectValue() + " EP.");
            }
            case APPLY_STATUS -> {
                var statusType = ability.getStatusEffectApplied();
                boolean apply = ability.getEffectValue() <= 0 || new java.util.Random().nextInt(100) < ability.getEffectValue();
                if (apply) {
                    target.addStatusEffect(StatusEffectFactory.create(statusType));
                    log.addEntry(target.getName() + " is now " + statusType + ".");
                } else {
                    log.addEntry(target.getName() + " resisted the effect.");
                }
            }
            case DEFENSE, EVADE, UTILITY -> {
                var statusType = ability.getStatusEffectApplied();
                if (statusType != null && statusType != StatusEffectType.NONE) {
                    user.addStatusEffect(StatusEffectFactory.create(statusType));
                    log.addEntry(user.getName() + " gains " + statusType + ".");
                } else if (ability.getAbilityEffectType() == AbilityEffectType.DEFENSE) {
                    user.addStatusEffect(StatusEffectFactory.create(StatusEffectType.DEFENSE_UP));
                    log.addEntry(user.getName() + " braces for impact.");
                } else if (ability.getEffectValue() > 0) {
                    user.heal(ability.getEffectValue());
                    log.addEntry(user.getName() + " recovers " + ability.getEffectValue() + " HP.");
                }
            }
            default -> {
                // Gracefully handle any unknown effect types
                log.addEntry("Ability type " + ability.getAbilityEffectType() + " not implemented.");
            }
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
