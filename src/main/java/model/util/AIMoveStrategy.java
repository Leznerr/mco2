package model.util;

import model.core.Character;
import model.battle.Move;

/**
 * <h2>AIMoveStrategy</h2>
 * <p>Strategy interface for AI-controlled move selection in battle. This functional interface
 * allows multiple interchangeable decision-making styles (e.g., aggressive, random, defensive)
 * without modifying battle or controller logic.</p>
 *
 * <p><strong>Design Principles:</strong></p>
 * <ul>
 *   <li><strong>Interface Segregation</strong> – defines only one method: {@code decideMove}.</li>
 *   <li><strong>Strategy Pattern</strong> – enables pluggable AI logic through composition.</li>
 *   <li><strong>MVC Compliance</strong> – used by {@code controller.AIController} only;
 *       models contain no AI logic.</li>
 * </ul>
 *
 * <p><strong>Usage:</strong></p>
 * <pre>{@code
 *   AIMoveStrategy strategy = new RandomStrategy();
 *   Move chosen = strategy.decideMove(aiCharacter, playerCharacter);
 * }</pre>
 */
@FunctionalInterface
public interface AIMoveStrategy {

    /**
     * Selects a valid move for the AI character based on the current battle state.
     *
     * @param botCharacter       the character controlled by the AI (non-null)
     * @param opponentCharacter  the opposing player-controlled character (non-null)
     * @return a valid {@link Move} that the AI will execute
     * @throws GameException if no valid move can be made or if inputs are invalid
     */
    Move decideMove(Character botCharacter, Character opponentCharacter) throws GameException;
}
