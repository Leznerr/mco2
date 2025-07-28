package model.item;

import model.util.GameException;

/**
 * Passive item that revives the wearer once per battle when they fall to 0 HP.
 */
public class PhoenixFeather extends PassiveItem {
    public PhoenixFeather() throws GameException {
        super("Phoenix Feather",
              "Legendary feather that revives the wearer once per battle.",
              "Rare");
    }

    @Override
    public MagicItem copy() {
        try {
            return new PhoenixFeather();
        } catch (GameException e) {
            throw new RuntimeException(e);
        }
    }
}
