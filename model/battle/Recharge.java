package model.battle;

import model.core.Character;
import model.util.Constants;
import model.util.GameException;
import model.util.InputValidator;

/**
 * Simple battle action that restores a small amount of EP.
 */
public final class Recharge implements MoveAction {

    @Override
    public String getName() {
        return "Recharge";
    }

    @Override
    public String getDescription() {
        return "Focus to regain a little EP.";
    }

    @Override
    public int getEpCost() {
        return 0;
    }

    @Override
    public void execute(Character user, Character target, CombatLog log) throws GameException {
        InputValidator.requireNonNull(user, "user");
        InputValidator.requireNonNull(log, "combat log");

        user.gainEp(Constants.RECHARGE_EP_GAIN);
        log.addEntry(user.getName() + " recharges and gains " + Constants.RECHARGE_EP_GAIN + " EP.");
    }
}
