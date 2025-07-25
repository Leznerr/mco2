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

    @Override
    public void execute(Character user, Character target, CombatLog log) throws GameException {
        InputValidator.requireNonNull(user, "user");
        InputValidator.requireNonNull(log, "combat log");

        if (!user.getInventory().getAllItems().contains(item)) {
            throw new GameException("Item not found in inventory.");
        }

        log.addEntry(user.getName() + " uses " + item.getName() + ".");
        user.getInventory().removeItem(item);
        // Actual item effects would be handled elsewhere
    }
}
