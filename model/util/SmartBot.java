package model.util;

import model.battle.AbilityMove;
import model.battle.ItemMove;
import model.battle.Move;
import model.battle.Recharge;
import model.core.Ability;
import model.core.AbilityEffectType;
import model.core.Character;
import model.item.MagicItem;
import model.item.SingleUseItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <h2>SmartBot</h2>
 *
 * <p>Heuristic based AI strategy that chooses sensible actions
 * depending on the current battle state. It prioritises healing
 * when wounded, restoring energy when low, and attacking otherwise.
 * Randomness is injected to avoid deterministic play.</p>
 */
public final class SmartBot implements AIMoveStrategy {

    /** Random generator used for tie‑breakers. */
    private final Random random;

    /**
     * Constructs a {@code SmartBot} using the supplied random source.
     *
     * @param random non-null {@link Random} instance
     * @throws GameException if {@code random} is null
     */
    public SmartBot(Random random) throws GameException {
        InputValidator.requireNonNull(random, "Random");
        this.random = random;
    }

    /**
     * Chooses the most appropriate {@link Move} based on the current state of the battle.
     * <p>
     * The AI logic includes:
     * <ul>
     *     <li>Healing when HP is low</li>
     *     <li>Restoring energy when EP is low</li>
     *     <li>Executing high-damage or lethal attacks when available</li>
     *     <li>Using defensive abilities when no offensive options remain</li>
     * </ul>
     * Falls back to {@link Recharge} when no viable move is found.
     *
     * @param bot      the AI-controlled {@link Character}
     * @param opponent the opposing {@link Character}
     * @return a {@link Move} decision for the current turn
     * @throws GameException if either character is null
     */
    @Override
    public Move decideMove(Character bot, Character opponent) throws GameException {
        InputValidator.requireNonNull(bot, "botCharacter");
        InputValidator.requireNonNull(opponent, "opponentCharacter");

        List<Move> healingMoves = new ArrayList<>();
        List<Move> attackMoves = new ArrayList<>();
        List<Move> energyMoves = new ArrayList<>();
        List<Move> defensiveMoves = new ArrayList<>();

        for (Ability a : bot.getAbilities()) {
            if (a.getEpCost() <= bot.getCurrentEp()) {
                switch (a.getAbilityEffectType()) {
                    case HEAL -> healingMoves.add(new AbilityMove(a));
                    case ENERGY_GAIN -> energyMoves.add(new AbilityMove(a));
                    case DEFENSE, EVADE -> defensiveMoves.add(new AbilityMove(a));
                    default -> attackMoves.add(new AbilityMove(a));
                }
            }
        }

        MagicItem eq = bot.getInventory().getEquippedItem();
        if (eq instanceof SingleUseItem su) {
            switch (su.getEffectType()) {
                case HEAL_HP -> healingMoves.add(new ItemMove(su));
                case RESTORE_EP -> energyMoves.add(new ItemMove(su));
                case GRANT_IMMUNITY -> defensiveMoves.add(new ItemMove(su));
                default -> {}
            }
        }

        int maxHp = bot.getMaxHp();
        int curHp = bot.getCurrentHp();
        int curEp = bot.getCurrentEp();

        int minAbilityCost = bot.getAbilities().stream()
                .mapToInt(Ability::getEpCost)
                .filter(c -> c > 0)
                .min().orElse(0);

        if (curHp <= maxHp / 3 && !healingMoves.isEmpty()) {
            return randomPick(healingMoves);
        }

        if (curEp < minAbilityCost && !energyMoves.isEmpty()) {
            return randomPick(energyMoves);
        }

        // Opportunistic kill
        Move lethal = attackMoves.stream()
                .filter(m -> m instanceof AbilityMove)
                .map(m -> (AbilityMove) m)
                .filter(am -> am.getAbility().getAbilityEffectType() == AbilityEffectType.DAMAGE)
                .filter(am -> am.getAbility().getEffectValue() >= opponent.getCurrentHp())
                .findFirst()
                .orElse(null);
        if (lethal != null) return lethal;

        if (!attackMoves.isEmpty()) {
            return chooseHighestDamage(attackMoves);
        }

        if (!defensiveMoves.isEmpty()) {
            return randomPick(defensiveMoves);
        }

        if (!energyMoves.isEmpty() && curEp < bot.getMaxEp()) {
            return randomPick(energyMoves);
        }

        return new Recharge();
    }

    /**
     * Chooses the highest-damage move from a list of attacks.
     * If multiple moves share the same top damage, one is selected randomly.
     *
     * @param attacks list of available attack moves
     * @return the chosen high-damage move
     */
    private Move chooseHighestDamage(List<Move> attacks) {
        int best = -1;
        List<Move> bestMoves = new ArrayList<>();
        for (Move m : attacks) {
            int dmg = 0;
            if (m instanceof AbilityMove am &&
                    am.getAbility().getAbilityEffectType() == AbilityEffectType.DAMAGE) {
                dmg = am.getAbility().getEffectValue();
            }
            if (dmg > best) {
                best = dmg;
                bestMoves.clear();
                bestMoves.add(m);
            } else if (dmg == best) {
                bestMoves.add(m);
            }
        }
        return randomPick(bestMoves.isEmpty() ? attacks : bestMoves);
    }

    /**
     * Selects a random move from the provided list.
     *
     * @param moves a non-empty list of candidate moves
     * @return one randomly chosen move
     */
    private Move randomPick(List<Move> moves) {
        return moves.get(random.nextInt(moves.size()));
    }
}
