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

    /**
     * Executes the Recharge move by restoring a fixed amount of energy points (EP) to the user.
     *
     * <p>This action costs no EP and is logged as a combat event.</p>
     *
     * @param user   the character performing the Recharge (must not be {@code null})
     * @param target unused for this move, can be {@code null}
     * @param log    the combat log used to record the action (must not be {@code null})
     * @throws GameException if {@code user} or {@code log} is {@code null}
     */
    @Override
    public void execute(Character user, Character target, CombatLog log) throws GameException {
        InputValidator.requireNonNull(user, "user");
        InputValidator.requireNonNull(log, "combat log");

        user.gainEp(Constants.RECHARGE_EP_GAIN);
        log.addEntry(user.getName() + " recharges and gains " + Constants.RECHARGE_EP_GAIN + " EP.");
    }
}
