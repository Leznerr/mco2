package model.item;

import model.util.GameException;

/**
 * Passive item granting immunity to the first status effect each battle.
 */
public class ElvenCloak extends PassiveItem {
    public ElvenCloak() throws GameException {
        super("Elven Cloak",
              "Shimmering cloak that negates the first status effect each battle.",
              "Uncommon");
    }

    @Override
    public MagicItem copy() {
        try {
            return new ElvenCloak();
        } catch (GameException e) {
            throw new RuntimeException(e);
        }
    }
}
