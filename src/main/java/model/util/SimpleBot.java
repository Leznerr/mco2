package model.util;

import model.battle.AbilityMove;
import model.battle.Move;
import model.core.Ability;
import model.core.Character;

import java.util.List;
import java.util.Random;

/**
 * <h2>SimpleBot</h2>
 *
 * <p>Simple random-move AI strategy for bot-controlled characters.</p>
 *
 * <p>Implements {@link AIMoveStrategy} to randomly select an available
 * ability as a {@link Move}. Used in the AI battle mode of the game.</p>
 *
 * <h3>Design</h3>
 * <ul>
 *   <li>Stateless, immutable, and thread-safe</li>
 *   <li>Randomness injected for testability</li>
 * </ul>
 */
public final class SimpleBot implements AIMoveStrategy {

    /** Random generator used for move selection. */
    private final Random random;

    /**
     * Constructs a {@code SimpleBot} with the given random seed source.
     *
     * @param random non-null {@link Random} instance
     * @throws GameException if {@code random} is null
     */
    public SimpleBot(Random random) throws GameException {
        InputValidator.requireNonNull(random, "Random");
        this.random = random;
    }

    /**
     * Selects a move by randomly picking one ability from the bot's available list.
     *
     * @param botCharacter      the AI-controlled character (non-null)
     * @param opponentCharacter the opposing character (non-null)
     * @return a valid {@link Move} representing a random ability
     * @throws GameException if no valid abilities are found or inputs are null
     */
    @Override
    public Move decideMove(Character botCharacter, Character opponentCharacter) throws GameException {
        InputValidator.requireNonNull(botCharacter, "botCharacter");
        InputValidator.requireNonNull(opponentCharacter, "opponentCharacter");

        List<Ability> abilities = botCharacter.getAbilities();
        if (abilities.isEmpty()) {
            throw new GameException("Bot has no available abilities to use.");
        }

        int index = random.nextInt(abilities.size());
        return new AbilityMove(abilities.get(index));
    }
}
