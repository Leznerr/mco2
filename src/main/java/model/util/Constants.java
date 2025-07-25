package model.util;

/**
 * <h2>Constants</h2>
 * 
 * <p>Global, immutable game-wide constants shared across the application.</p>
 *
 * <p>This utility class centralizes tuning parameters for game balancing,
 * enforcing consistency across all layers: model, controller, and view.
 * It is non-instantiable by design and all fields are declared
 * {@code public static final} as per UML specification.</p>
 *
 * <h3>Design Rationale:</h3>
 * <ul>
 *   <li><strong>Non-instantiable utility class</strong> – private constructor blocks creation.</li>
 *   <li><strong>Shared configuration</strong> – avoids magic numbers scattered across code.</li>
 *   <li><strong>MVC Compliant</strong> – contains no logic; safely used by model, controller, and view layers.</li>
 * </ul>
 */
public final class Constants {

    // ────────────────────────────────────────────────────────
    // Character & roster limits
    // ────────────────────────────────────────────────────────

    /** Maximum number of abilities a Character can equip. */
    public static final int NUM_ABILITIES_PER_CHAR = 3;

    /** Maximum number of {@code Character}s a {@code Player} can own. */
    public static final int MAX_CHARACTERS_PER_PLAYER = 6;

    // ────────────────────────────────────────────────────────
    // Base statistics
    // ────────────────────────────────────────────────────────

    /** Base health points (HP) before race/class bonuses. */
    public static final int BASE_HP = 100;

    /** Base energy points (EP) before race/class bonuses. */
    public static final int BASE_EP = 50;

    /** Maximum EP cost allowed for any ability or item effect. */
    public static final int MAX_EP_COST = 50;

    /** Maximum numeric value for damage/healing/effect output. */
    public static final int MAX_EFFECT_VALUE = 100;

    /** The maximum number of status effects a character can have at once. */
    public static final int MAX_STATUS_EFFECTS = 5;

    // ────────────────────────────────────────────────────────
    // Battle economy tuning
    // ────────────────────────────────────────────────────────

    /** EP cost of the universal "Defend" action. */
    public static final int DEFEND_EP_COST = 5;

    /** EP gained from the "Recharge" battle action. */
    public static final int RECHARGE_EP_GAIN = 5;

    /** EP automatically regained at end of each round. */
    public static final int ROUND_EP_REGEN = 5;

    // ────────────────────────────────────────────────────────
    // Reward thresholds
    // ────────────────────────────────────────────────────────

    /** Number of wins needed to earn a random magic item. */
    public static final int WINS_PER_REWARD = 3;

    /**
     * Private constructor – this class must never be instantiated.
     *
     * @throws UnsupportedOperationException always
     */
    private Constants() {
        throw new UnsupportedOperationException("Utility class – instantiation not allowed");
    }


// ────────────────────────────────────────────────────────
// Mage Ability Stats
// ────────────────────────────────────────────────────────
public static final int ARCANE_BOLT_DMG = 20;
public static final int ARCANE_BOLT_COST = 5;
public static final int ARCANE_BLAST_DMG = 65;
public static final int ARCANE_BLAST_COST = 30;
public static final int MANA_CHANNEL_GAIN = 15;
public static final int MANA_CHANNEL_COST = 0;
public static final int LESSER_HEAL_HP = 40;
public static final int LESSER_HEAL_COST = 15;
public static final int ARCANE_SHIELD_COST = 12;

// ────────────────────────────────────────────────────────
// Rogue Ability Stats
// ────────────────────────────────────────────────────────
public static final int SHIV_DMG = 20;
public static final int SHIV_COST = 5;
public static final int BACKSTAB_DMG = 35;
public static final int BACKSTAB_COST = 15;
public static final int FOCUS_GAIN = 10;
public static final int FOCUS_COST = 0;
public static final int SMOKE_BOMB_COST = 15;
public static final int SNEAK_ATTACK_DMG = 45;
public static final int SNEAK_ATTACK_COST = 25;

// ────────────────────────────────────────────────────────
// Warrior Ability Stats
// ────────────────────────────────────────────────────────
public static final int CLEAVE_DMG = 20;
public static final int CLEAVE_COST = 5;
public static final int SHIELD_BASH_DMG = 35;
public static final int SHIELD_BASH_COST = 15;
public static final int IRONCLAD_DEFENSE_COST = 15;
public static final int BLOODLUST_HP = 30;
public static final int BLOODLUST_COST = 12;
public static final int RALLYING_CRY_GAIN = 12;
public static final int RALLYING_CRY_COST = 0;


public static final String SAVE_FILE_PATH = "ff_tactics_save.dat";
public static final String HALL_OF_FAME_SAVE_PATH = "save/hall_of_fame.dat";

}
