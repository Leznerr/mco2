package model.battle;

import model.core.Character;
import model.util.GameException;

/**
 * Generic contract representing <em>any</em> actionable move that can be
 * presented to a player or AI during the battle phase of
 * <strong>Fatal Fantasy: Tactics</strong>.
 * <p>
 * Implementations include:
 * <ul>
 *   <li>{@code AbilityMove} – class-specific special moves</li>
 *   <li>{@code MoveAction} – universal <em>Defend</em>/<em>Recharge</em></li>
 *   <li>{@code ItemMove} – consumable items that act like moves</li>
 * </ul>
 * The interface purposefully contains <em>no execution logic</em>; the
 * concrete battle engine or controller decides how to apply an action
 * based on runtime type-checks or a strategy map, keeping this contract
 * stable and open for extension.
 */
public interface Move {

    /**
     * Machine-readable & display name used in menus and logs.
     *
     * @return non-blank move name
     */
    String getName();

    /**
     * Short, human-friendly explanation shown in tool-tips or the combat log.
     *
     * @return non-blank description
     */
    String getDescription();

    /**
     * Energy Point cost to perform the move.
     *
     * @return value &ge; 0
     */
    int getEpCost();

    /**
     * Executes the move on the target character.
     *
     * @param user       the character using the move (non-null)
     * @param target     the character targeted by the move (non-null)
     * @param combatLog  the combat log to record actions (non-null)
     * @throws GameException if the move fails or an effect cannot be applied
     */
    void execute(Character user, Character target, CombatLog combatLog) throws GameException;
}
