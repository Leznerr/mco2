package controller;

import model.battle.Battle;
import model.battle.CombatLog;
import model.battle.Move;
import model.core.Character;
import model.item.SingleUseItem;
import model.util.GameException;
import model.util.InputValidator;
import view.BattleView;

import java.util.*;

/**
 * Central orchestrator for a two-character, turn-based battle.
 *
 * <h3>BattleView contract (minimal)</h3>
 * <pre>
 * void displayBattleStart(Character c1, Character c2);
 * void displayTurnResults(CombatLog log);
 * void displayBattleEnd(Character winner);
 * </pre>
 *
 * <p>The controller is stateless between battles – reuse a single instance
 * and call {@link #startBattle(Character, Character)} whenever you need
 * a fresh bout.</p>
 */
public final class BattleController {

    /* -------------------------------------------------------- IMMUTABLES */
    private final BattleView view;

    /* ----------------------------------------------------------- SESSION */
    private Battle battle; // null ⇢ idle
    private final Map<Character, Move> selections = new HashMap<>(2);

    /* ===================================================== CONSTRUCTION */

    public BattleController(BattleView battleView) throws GameException {
        InputValidator.requireNonNull(battleView, "battleView");
        this.view = battleView;
    }

    /* ================================================= PUBLIC API */

    /**
     * Kicks off a new battle session.
     */
    public void startBattle(Character c1, Character c2) throws GameException {
        InputValidator.requireNonNull(c1, "character 1");
        InputValidator.requireNonNull(c2, "character 2");

        if (!c1.isAlive() || !c2.isAlive()) {
            throw new GameException("Both characters must be alive to start a battle.");
        }

        battle = new Battle(c1, c2);
        selections.clear();
        view.displayBattleStart(c1, c2);
    }

    /**
     * Called by UI (or AI) after a character has chosen a move.
     *
     * @throws GameException if battle not running, params null, or character
     *         doesn’t belong to the current battle.
     */
    public void submitMove(Character user, Move move) throws GameException {
        ensureRunning();
        InputValidator.requireNonNull(user, "user");
        InputValidator.requireNonNull(move, "move");

        if (!belongsToBattle(user)) {
            throw new GameException("Character is not part of the current battle.");
        }

        selections.put(user, move);

        if (selections.size() == 2) { // both combatants have chosen
            executeTurn();
        }
    }

    /**
     * Handles a request to use a single-use item in battle.
     * Applies the item's effect in the battle context, updates logs,
     * and removes the item from the user's inventory.
     *
     * @param user the character using the item
     * @param item the single-use item to use
     * @throws GameException if validation fails or item cannot be used
     */
    public void useSingleUseItem(Character user, SingleUseItem item) throws GameException {
        ensureRunning();
        InputValidator.requireNonNull(user, "user");
        InputValidator.requireNonNull(item, "item");

        if (!belongsToBattle(user)) {
            throw new GameException("Character is not part of the current battle.");
        }
        // Ensure the user has the item in inventory
        if (!user.getInventory().getAllItems().contains(item)) {
            throw new GameException("Item is not in user's inventory.");
        }
        // Apply item effect. You must implement this effect method according to your game logic.
        // Example: item.applyEffect(user, battle, battle.getCombatLog());
        // (You'll need to pass the appropriate target if applicable)

        // -- PLACEHOLDER: Apply effect logic here (should be part of SingleUseItem, or use effect strategy) --
        // Example (pseudo):
        // item.applyEffect(user, target, battle, battle.getCombatLog());
        // For now, just log use:
        battle.getCombatLog().log(user.getName() + " used item: " + item.getName());

        // Remove the item from inventory after use
        user.getInventory().removeItem(item);

        // Optionally, refresh view/turn state as needed
        // view.displayTurnResults(battle.getCombatLog());
    }

    /* ================================================= INTERNAL FLOW */

    private void executeTurn() throws GameException {
        CombatLog log = battle.getCombatLog();

        /* order by priority – higher first (ties resolved arbitrarily) */
        List<Turn> order = buildTurnOrder();
        for (Turn t : order) {
            if (t.actor.isAlive() && t.target.isAlive()) {
                t.move.execute(t.actor, t.target, log);
            }
        }

        view.displayTurnResults(log);
        selections.clear(); // prepare for next round

        if (battleEnded()) {
            Character winner = battle.getCharacter1().isAlive()
                    ? battle.getCharacter1()
                    : battle.getCharacter2();
            view.displayBattleEnd(winner);
            battle = null; // back to idle state
        }
    }

    /* ================================================= HELPER STRUCTS */

    private record Turn(Character actor, Character target, Move move) implements Comparable<Turn> {
        /** Higher priority executes first */
        @Override
        public int compareTo(Turn o) {
            // If Move exposes priority use it; otherwise default to 0
            int myP = (move instanceof Prioritised p) ? p.getPriority() : 0;
            int hisP = (o.move instanceof Prioritised p) ? p.getPriority() : 0;
            return Integer.compare(hisP, myP); // descending
        }
    }

    /**
     * Optional interface: implement in moves that need priority.
     */
    public interface Prioritised {
        int getPriority();
    }

    /* ================================================= SMALL UTILS */

    private List<Turn> buildTurnOrder() {
        Character c1 = battle.getCharacter1();
        Character c2 = battle.getCharacter2();
        Move m1 = selections.get(c1);
        Move m2 = selections.get(c2);

        List<Turn> list = new ArrayList<>(2);
        list.add(new Turn(c1, c2, m1));
        list.add(new Turn(c2, c1, m2));
        Collections.sort(list); // uses compareTo()
        return list;
    }

    private boolean belongsToBattle(Character c) {
        return c == battle.getCharacter1() || c == battle.getCharacter2();
    }

    private boolean battleEnded() {
        return !battle.getCharacter1().isAlive() || !battle.getCharacter2().isAlive();
    }

    private void ensureRunning() throws GameException {
        if (battle == null) {
            throw new GameException("No active battle – call startBattle() first.");
        }
    }
}
