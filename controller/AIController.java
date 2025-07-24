package controller;

import model.core.Character;
import model.battle.Move;
import model.util.GameException;
import model.util.InputValidator;
import model.util.AIMoveStrategy;

/**
 * Controller responsible for AI-controlled move selection
 * in battles using a provided {@link AIMoveStrategy}.
 * <p>Follows the Strategy pattern to allow pluggable AI algorithms.</p>
 */
public class AIController {

    /** AI strategy used for move selection (immutable once set). */
    private final AIMoveStrategy strategy;

    /**
     * Constructs an AI controller using the specified strategy.
     *
     * @param strategy non-null AI move strategy
     * @throws GameException if {@code strategy} is {@code null}
     */
    public AIController(AIMoveStrategy strategy) throws GameException {
        InputValidator.requireNonNull(strategy, "AIMoveStrategy");
        this.strategy = strategy;
    }

    /**
     * Requests the next move decision for the AI-controlled character.
     *
     * @param botCharacter the AI-controlled character (non-null)
     * @param opponentCharacter the opponent character (non-null)
     * @return the selected {@link Move}
     * @throws GameException if any parameter is invalid or strategy fails
     */
    public Move requestMove(Character botCharacter, Character opponentCharacter) throws GameException {
        InputValidator.requireNonNull(botCharacter, "botCharacter");
        InputValidator.requireNonNull(opponentCharacter, "opponentCharacter");
        return strategy.decideMove(botCharacter, opponentCharacter);
    }
}
