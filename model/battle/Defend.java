package model.battle;

import model.core.Character;
import model.util.Constants;
import model.util.GameException;
import model.util.InputValidator;
import model.util.StatusEffectFactory;
import model.util.StatusEffectType;

/**
 * Battle action representing the universal "Defend" move.
 *
 * <p>The action costs a small amount of EP and simply records that the
 * character defended.  Actual damage reduction mechanics are left to the
 * combat system.</p>
 */
public final class Defend implements MoveAction {

    @Override
    public String getName() {
        return "Defend";
    }

    @Override
    public String getDescription() {
        return "Brace yourself to reduce incoming damage.";
    }

    @Override
    public int getEpCost() {
        return Constants.DEFEND_EP_COST;
    }

    @Override
    public void execute(Character user, Character target, CombatLog log) throws GameException {
        InputValidator.requireNonNull(user, "user");
        InputValidator.requireNonNull(log, "combat log");

        if (!user.spendEp(Constants.DEFEND_EP_COST)) {
            throw new GameException(user.getName() + " does not have enough EP to defend.");
        }

        user.addStatusEffect(StatusEffectFactory.create(StatusEffectType.DEFENSE_UP));
        log.addEntry(user.getName() + " takes a defensive stance.");
    }
}
