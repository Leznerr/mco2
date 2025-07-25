package model.service;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import model.core.RaceBonus;
import model.core.RaceType;
import model.util.GameException;
import model.util.InputValidator;

/**
 * <h2>RaceService</h2>
 * <p>A singleton service class that provides immutable, read-only access
 * to {@link RaceBonus} data and race flavor descriptions in
 * <em>Fatal Fantasy: Tactics</em>.</p>
 *
 * <p><strong>Design Highlights:</strong></p>
 * <ul>
 *   <li><strong>Stateless & Thread-safe:</strong> All data is preloaded in static final maps.</li>
 *   <li><strong>Singleton Pattern:</strong> Public access via {@link #INSTANCE}.</li>
 *   <li><strong>Clean MVC Separation:</strong> No UI dependencies or rendering logic.</li>
 * </ul>
 */
public final class RaceService {

    /* -------------------------------------------------------------
     * Static Immutable Data Structures
     * ----------------------------------------------------------- */

    private static final Map<RaceType, RaceBonus> RACE_BONUSES =
        Map.copyOf(initializeBonuses());

    private static final Map<RaceType, String> RACE_DESCRIPTIONS =
        Map.of(
            RaceType.HUMAN , "Versatile adventurers equally at home with blade or spell.",
            RaceType.ELF   , "Graceful folk attuned to nature and the arcane.",
            RaceType.GNOME , "Inventive tinkers whose wit outshines their stature.",
            RaceType.DWARF , "Stalwart warriors tempered in stone-forged halls."
        );

    /** Singleton instance – race data is static and immutable. */
    public static final RaceService INSTANCE = new RaceService();

    /**
     * Private constructor to enforce singleton utility design.
     */
    private RaceService() { /* no instantiation */ }

    /* -------------------------------------------------------------
     * Public API
     * ----------------------------------------------------------- */

    /**
     * Returns the {@link RaceBonus} stat adjustments associated with the given race.
     *
     * @param raceType non-null race type
     * @return immutable bonus record for that race
     * @throws GameException if {@code raceType} is {@code null}
     */
    public RaceBonus getBonusFor(RaceType raceType) throws GameException {
        InputValidator.requireNonNull(raceType, "RaceType");
        return RACE_BONUSES.get(raceType);
    }

    /**
     * Returns a short description of the race for UI display.
     *
     * @param raceType non-null race type
     * @return short flavor text for tooltips or selection screens
     * @throws GameException if {@code raceType} is {@code null}
     */
    public String getRaceDescription(RaceType raceType) throws GameException {
        InputValidator.requireNonNull(raceType, "RaceType");
        return RACE_DESCRIPTIONS.get(raceType);
    }

    /**
     * Returns all playable races.
     *
     * @return list of available {@link RaceType} enums
     */
    public List<RaceType> getAvailableRaces() {
        return Arrays.asList(RaceType.values());
    }

    /* -------------------------------------------------------------
     * Private Initializer
     * ----------------------------------------------------------- */

    private static Map<RaceType, RaceBonus> initializeBonuses() {
        EnumMap<RaceType, RaceBonus> map = new EnumMap<>(RaceType.class);
        map.put(RaceType.HUMAN , new RaceBonus(0 ,  0, 0));
        map.put(RaceType.ELF   , new RaceBonus(0 , 10, 1));
        map.put(RaceType.GNOME , new RaceBonus(-5, 15, 1));
        map.put(RaceType.DWARF , new RaceBonus(20, -5, 0));
        return map;
    }
}
