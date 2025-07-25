package model.item;

import model.battle.CombatLog;
import model.core.Character;
import model.util.Constants;
import model.util.GameException;
import model.util.InputValidator;

/**
 * <h2>SingleUseItem</h2>
 *
 * A {@link MagicItem} that is consumed after one activation in
 * <i>Fatal Fantasy: Tactics</i>.
 *
 * <p>This class is <strong>immutable</strong> and holds only metadata.
 * All gameplay logic (e.g., activation effects) is handled externally by
 * the controller or battle system.</p>
 *
 * <h3>Responsibilities & Design</h3>
 * <ul>
 *   <li>Stores descriptive item data (via {@link MagicItem})</li>
 *   <li>Used once, then removed from {@link Inventory}</li>
 *   <li>Supports defensive copying via {@code copy()}</li>
 *   <li>No battle logic included – adheres to SRP and MVC</li>
 * </ul>
 *
 * @see PassiveItem
 * @see Inventory
 */
public final class SingleUseItem extends MagicItem {

    /** Specific effect this item triggers when consumed. */
    private final SingleUseEffectType effectType;

    /** Numeric magnitude of the effect (e.g., HP restored). */
    private final int effectValue;

    /**
     * Constructs a single-use magic item.
     *
     * @param name        display name (non-blank)
     * @param description tooltip or flavour text (non-blank)
     * @param rarity      rarity label (non-blank)
     * @param effectType  type of effect when used (non-null)
     * @param effectValue numeric value of the effect (1..MAX_EFFECT_VALUE)
     * @throws GameException if any argument fails validation
     */
    public SingleUseItem(String name, String description, String rarity,
                         SingleUseEffectType effectType, int effectValue)
            throws GameException {
        super(name, description, ItemType.SINGLE_USE, rarity);

        InputValidator.requireNonNull(effectType, "effect type");
        InputValidator.requireRange(effectValue, 1,
                Constants.MAX_EFFECT_VALUE, "effect value");

        this.effectType = effectType;
        this.effectValue = effectValue;
    }

    /** @return the effect type */
    public SingleUseEffectType getEffectType() {
        return effectType;
    }

    /** @return the numeric effect value */
    public int getEffectValue() {
        return effectValue;
    }



    /**
     * Creates a deep copy of this item.
     *
     * @return new {@code SingleUseItem} with identical state
     */
    @Override
    public MagicItem copy() {
        return new SingleUseItem(getName(), getDescription(), getRarity(),
                effectType, effectValue);
    }

    /**
     * Applies this item's effect to the given user within a battle.
     *
     * @param user the character consuming the item (non-null)
     * @param log  combat log to record actions (non-null)
     * @throws GameException if the effect cannot be applied
     */
    public void applyEffect(Character user, CombatLog log) throws GameException {
        InputValidator.requireNonNull(user, "item user");
        InputValidator.requireNonNull(log,  "combat log");

        switch (effectType) {
            case HEAL_HP -> {
                user.heal(effectValue);
                log.addEntry(user.getName() + " uses " + getName()
                        + " and restores " + effectValue + " HP!");
            }
            case RESTORE_EP -> {
                user.gainEp(effectValue);
                log.addEntry(user.getName() + " uses " + getName()
                        + " and gains " + effectValue + " EP!");
            }
            case REVIVE -> {
                if (user.isAlive()) {
                    log.addEntry(user.getName() + " uses " + getName()
                            + " but is already conscious.");
                } else {
                    int restore = user.getMaxHp() * effectValue / 100;
                    user.heal(restore);
                    log.addEntry(user.getName() + " is revived by " + getName()
                            + " with " + restore + " HP!");
                }
            }
            default -> throw new GameException("Unhandled single-use effect: "
                    + effectType);
        }
    }
}
