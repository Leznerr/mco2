package model.battle;

import model.core.Character;
import model.util.GameException;
import model.util.InputValidator;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <h2>LevelingSystem</h2>
 *
 * A domain-service that houses all experience-point (XP) and level-up rules for
 * <em>Fatal&nbsp;Fantasy: Tactics</em>. Isolated here, game-balance tweaks can
 * be made without touching entity or controller code (SRP ✓, OCP ✓).
 *
 * <p><strong>Thread-safety:</strong> class is immutable and stateless.</p>
 */
public final class LevelingSystem {

    /* ───────────────────────────────── Constants ───────────────────────── */

    /** Immutable table: <em>level&nbsp;→&nbsp;minimum cumulative XP</em>. */
    private static final Map<Integer, Integer> LEVEL_THRESHOLDS;
    /** Hit-points gained per level-up. */
    private static final int HP_GAIN_PER_LEVEL = 10;
    /** Energy-points gained per level-up. */
    private static final int EP_GAIN_PER_LEVEL = 5;

    static {
        Map<Integer, Integer> tmp = new LinkedHashMap<>();
        tmp.put(1,   0);
        tmp.put(2, 100);
        tmp.put(3, 250);
        tmp.put(4, 450);
        tmp.put(5, 700);       // Extend as game expands
        LEVEL_THRESHOLDS = Collections.unmodifiableMap(tmp);
    }

    /** Suppress instantiation. */
    private LevelingSystem() {
        throw new AssertionError("Utility class – do not instantiate");
    }

    /* ───────────────────────────── Public API ──────────────────────────── */

    /**
     * Calculates XP awarded to {@code winner} for defeating {@code loser}.
     * <pre>XP = 25 + 10 × loser.level</pre>
     *
     * @throws GameException if any argument is {@code null} or both references
     *                       point to the same object
     */
    public static int calculateXpGained(Character winner, Character loser)
            throws GameException {

        InputValidator.requireNonNull(winner, "winner");
        InputValidator.requireNonNull(loser,  "loser");
        if (winner == loser) {
            throw new GameException("Winner and loser cannot be identical");
        }
        return 25 + (10 * loser.getLevel());
    }

    /**
     * Evaluates whether {@code character} should level-up given its current XP.
     * If so, raises the level, increases max HP/EP, and fully restores both.
     *
     * @return {@code true} if at least one level was gained; otherwise {@code false}
     * @throws GameException if {@code character} is {@code null}
     */
    public static boolean processLevelUp(Character character)
            throws GameException {

        InputValidator.requireNonNull(character, "character");

        int currentLevel = character.getLevel();
        int targetLevel  = highestLevelForXp(character.getXp());

        if (targetLevel <= currentLevel) {
            return false; 
        }

        int levelsGained = targetLevel - currentLevel;
        character.setLevel(targetLevel);

        int newMaxHp = character.getMaxHp() + HP_GAIN_PER_LEVEL * levelsGained;
        int newMaxEp = character.getMaxEp() + EP_GAIN_PER_LEVEL * levelsGained;
        character.setMaxStats(newMaxHp, newMaxEp); // also heals/energises

        return true;
    }

    /* ─────────────────────────── Helper logic ─────────────────────────── */

    /** Returns highest level whose XP threshold ≤ {@code xp}. */
    private static int highestLevelForXp(int xp) {
        int lvl = 1;
        for (Map.Entry<Integer, Integer> e : LEVEL_THRESHOLDS.entrySet()) {
            if (xp >= e.getValue()) {
                lvl = e.getKey();
            } else {
                break; // thresholds are ascending
            }
        }
        return lvl;
    }
}
