package model.battle;

import model.core.Character;
import model.item.SingleUseItem;
import model.util.GameException;
import model.util.InputValidator;

/**
 * Wraps the use of a {@link SingleUseItem} as a {@link Move} so it can
 * participate in normal turn execution.
 */
public final class ItemMove implements Move {

    private final SingleUseItem item;

    public ItemMove(SingleUseItem item) throws GameException {
        InputValidator.requireNonNull(item, "item");
        this.item = item;
    }

    @Override
    public String getName() {
        return "Use " + item.getName();
    }

    @Override
    public String getDescription() {
        return item.getDescription();
    }

    @Override
    public int getEpCost() {
        return 0;
    }

    /**
     * Executes the item-based move by applying the item's effect and consuming it from the inventory.
     *
     * <p>Checks if the item exists in the user's inventory, logs the usage, applies its effect,
     * and then removes it.</p>
     *
     * @param user   the character using the item (must not be {@code null})
     * @param target the character receiving the effect of the item
     * @param log    the combat log used to record the action
     * @throws GameException if the item is not in the user's inventory
     */
    @Override
    public void execute(Character user, Character target, CombatLog log) throws GameException {
        InputValidator.requireNonNull(user, "user");
        InputValidator.requireNonNull(log, "combat log");

        if (!user.getInventory().getAllItems().contains(item)) {
            throw new GameException("Item not found in inventory.");
        }

        log.addEntry(user.getName() + " uses " + item.getName() + ".");
        item.applyEffect(user, target, log);
        user.getInventory().useSingleUseItem(item);
    }
}
