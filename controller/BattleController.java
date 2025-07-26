package controller;

import model.battle.Battle;
import model.battle.CombatLog;
import model.battle.Move;
import model.battle.Defend;
import model.battle.Recharge;
import model.battle.ItemMove;
import model.battle.AbilityMove;
import model.core.Character;
import model.core.Ability;
import model.core.Player;
import controller.GameManagerController;
import model.item.SingleUseItem;
import model.item.PassiveItem;
import model.battle.LevelingSystem;
import model.util.Constants;
import model.util.GameException;
import model.util.InputValidator;
import view.BattleView;
import controller.AIController;
import java.util.stream.Collectors;

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
    private final GameManagerController gameManagerController;
    private final Player player1;
    private final Player player2;

    /* ----------------------------------------------------------- SESSION */
    private Battle battle; // null ⇢ idle
    private final Map<Character, Move> selections = new HashMap<>(2);

    // AI support
    private AIController aiController;
    private Character aiCharacter;
    private Character humanOpponent;

    /* ===================================================== CONSTRUCTION */

    public BattleController(BattleView battleView) throws GameException {
        this(battleView, null, null, null);
    }

    public BattleController(BattleView battleView,
                            GameManagerController gameManagerController,
                            Player player1,
                            Player player2) throws GameException {
        InputValidator.requireNonNull(battleView, "battleView");
        this.view = battleView;
        this.gameManagerController = gameManagerController;
        this.player1 = player1;
        this.player2 = player2;
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
        aiController = null;
        aiCharacter = null;
        humanOpponent = null;
        view.displayBattleStart(c1, c2);
        updatePlayerPanels();
        startRound();
    }

    /**
     * Starts a battle where {@code bot} is controlled by an AI.
     * The opposing human character must choose moves via the UI.
     */
    public void startBattleVsBot(Character human, Character bot, AIController ai) throws GameException {
        InputValidator.requireNonNull(human, "human");
        InputValidator.requireNonNull(bot, "bot");
        InputValidator.requireNonNull(ai, "aiController");

        startBattle(human, bot);
        this.aiController = ai;
        this.aiCharacter = bot;
        this.humanOpponent = human;

        queueAIMove(); // Bot selects its first move immediately
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
     * Resolves a player choice string from the UI into an actionable move.
     * The choice may refer to an ability or a usable magic item.
     */
    public void handlePlayerChoice(Character user, String choice) throws GameException {
        ensureRunning();
        InputValidator.requireNonNull(user, "user");
        InputValidator.requireNonBlank(choice, "choice");

        if (!belongsToBattle(user)) {
            throw new GameException("Character is not part of the current battle.");
        }

        // Item choice is prefixed with "Item: "
        if (choice.startsWith("Item: ")) {
            String name = choice.substring(6);
            Optional<SingleUseItem> itemOpt = user.getInventory().getAllItems().stream()
                    .filter(i -> i instanceof SingleUseItem && i.getName().equals(name))
                    .map(i -> (SingleUseItem) i)
                    .findFirst();
            if (itemOpt.isPresent()) {
                submitMove(user, new ItemMove(itemOpt.get()));
                return;
            }
        }

        // Otherwise treat as ability name
        Optional<Ability> abilityOpt = user.getAbilities().stream()
                .filter(a -> a.getName().equals(choice))
                .findFirst();
        if (abilityOpt.isPresent()) {
            Ability a = abilityOpt.get();
            if (a.getEpCost() > user.getCurrentEp()) {
                battle.getCombatLog().addEntry(user.getName() + " lacks EP for " + a.getName());
                view.displayTurnResults(battle.getCombatLog());
                return;
            }
            submitMove(user, new AbilityMove(a));
        } else {
            battle.getCombatLog().addEntry("Unknown action: " + choice);
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

        // Verify item exists, apply its effect, and remove it from inventory
        if (!user.getInventory().getAllItems().contains(item)) {
            throw new GameException("Item not found in inventory.");
        }

        CombatLog log = battle.getCombatLog();
        item.applyEffect(user, log);
        user.getInventory().useSingleUseItem(item);
        updatePlayerPanels();
    }

    /** Submits a defend action for the given character. */
    public void defend(Character user) throws GameException {
        submitMove(user, new Defend());
    }

    /** Submits a recharge action for the given character. */
    public void recharge(Character user) throws GameException {
        submitMove(user, new Recharge());
    }

    /* ================================================= INTERNAL FLOW */

    private void executeTurn() throws GameException {
        CombatLog log = battle.getCombatLog();

        /* order by priority – higher first (ties resolved arbitrarily) */
        List<Turn> order = buildTurnOrder();
        for (Turn t : order) {
            if (!t.actor.isAlive()) continue;

            if (t.actor.isStunned()) {
                log.addEntry(t.actor.getName() + " is stunned and cannot act.");
            } else if (t.target.isAlive()) {
                t.move.execute(t.actor, t.target, log);
            }

            t.actor.processEndOfTurnEffects(log);
            if (battleEnded()) break;
        }

        view.displayTurnResults(log);
        updatePlayerPanels();
        selections.clear(); // prepare for next round

        if (battleEnded()) {
            Character winner = battle.getCharacter1().isAlive()
                    ? battle.getCharacter1()
                    : battle.getCharacter2();
            Character loser = (winner == battle.getCharacter1())
                    ? battle.getCharacter2() : battle.getCharacter1();

            // Award XP and handle win persistence if players are known
            if (gameManagerController != null) {
                Player winPlayer = (winner == battle.getCharacter1()) ? player1 : player2;
                if (winPlayer != null) {
                    int xp = LevelingSystem.calculateXpGained(winner, loser);
                    winner.addXp(xp);
                    log.addEntry(winner.getName() + " gains " + xp + " XP.");
                    if (gameManagerController != null) {
                        gameManagerController.handlePlayerWin(winPlayer, winner);
                    }
                }
            }

            view.displayBattleEnd(winner);
            updatePlayerPanels();
            battle = null; // back to idle state
            aiController = null;
        }
        else {
            battle.nextRound();
            startRound();
            if (aiController != null) {
                queueAIMove();
            }
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

    private void queueAIMove() throws GameException {
        if (aiController != null && aiCharacter != null && humanOpponent != null) {
            Move aiMove = aiController.requestMove(aiCharacter, humanOpponent);
            selections.put(aiCharacter, aiMove);
        }
    }

    private void startRound() throws GameException {
        ensureRunning();
        CombatLog log = battle.getCombatLog();
        log.addEntry("--- Round " + battle.getRoundNumber() + " Begins ---");

        processRoundStartFor(battle.getCharacter1(), log);
        processRoundStartFor(battle.getCharacter2(), log);

        view.displayTurnResults(log);
        updatePlayerPanels();
    }

    private void processRoundStartFor(Character c, CombatLog log) throws GameException {
        c.gainEp(Constants.ROUND_EP_REGEN);
        if (c.getInventory().getEquippedItem() instanceof PassiveItem p) {
            applyPassiveItemEffect(c, p, log);
        }
        c.processStartOfTurnEffects(log);
    }

    private void applyPassiveItemEffect(Character c, PassiveItem item, CombatLog log) throws GameException {
        String name = item.getName();
        switch (name) {
            case "Copper Ring" -> {
                c.gainEp(5);
                log.addEntry(c.getName() + " gains 5 EP from " + name + ".");
            }
            case "Silver Amulet" -> {
                c.heal(5);
                log.addEntry(c.getName() + " gains 5 HP from " + name + ".");
            }
            case "Golden Dragon Scale" -> log.addEntry(c.getName() + " is shielded by " + name + ".");
            default -> log.addEntry("Item effect for " + name + " not implemented.");
        }
    }

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

    /** Updates all player panels in the view to reflect current state. */
    private void updatePlayerPanels() throws GameException {
        ensureRunning();
        Character c1 = battle.getCharacter1();
        Character c2 = battle.getCharacter2();

        view.setPlayerNameAndCharName(1,
                c1.getName() + " - " + c1.getClassType() + "/" + c1.getRaceType());
        view.setPlayerNameAndCharName(2,
                c2.getName() + " - " + c2.getClassType() + "/" + c2.getRaceType());

        view.setPlayerStatus(1, formatStatus(c1));
        view.setPlayerStatus(2, formatStatus(c2));

        view.setPlayerAbilitiesItems(1, buildAbilityList(c1));
        view.setPlayerAbilitiesItems(2, buildAbilityList(c2));

        view.updateAbilityDropdown(1, abilityNames(c1));
        view.updateAbilityDropdown(2, abilityNames(c2));
    }

    private List<String> abilityNames(Character c) {
        List<String> names = new ArrayList<>();
        for (Ability a : c.getAbilities()) {
            names.add(a.getName());
        }
        c.getInventory().getAllItems().stream()
                .filter(i -> i instanceof SingleUseItem)
                .forEach(i -> names.add("Item: " + i.getName()));
        return names;
    }

    private String buildAbilityList(Character c) {
        StringBuilder sb = new StringBuilder();
        for (var a : c.getAbilities()) {
            sb.append(a.getName()).append(" (").append(a.getEpCost()).append(" EP)").append("\n");
        }
        for (var item : c.getInventory().getAllItems()) {
            sb.append("Item: ").append(item.getName()).append("\n");
        }
        if (c.getInventory().getEquippedItem() != null) {
            sb.append("Equipped: ").append(c.getInventory().getEquippedItem().getName());
        }
        return sb.toString();
    }

    private String formatStatus(Character c) {
        String base = String.format("HP %d/%d | EP %d/%d", c.getCurrentHp(), c.getMaxHp(),
                c.getCurrentEp(), c.getMaxEp());
        if (!c.getActiveStatusEffects().isEmpty()) {
            String statuses = c.getActiveStatusEffects().stream()
                    .map(se -> se.getType().name())
                    .collect(Collectors.joining(","));
            return base + " | " + statuses;
        }
        return base;
    }
}
