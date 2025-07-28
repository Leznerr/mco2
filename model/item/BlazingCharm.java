package model.item;

import model.util.GameException;

/**
 * Single-use item that damages the opponent when activated.
 */
public class BlazingCharm extends SingleUseItem {
    public BlazingCharm() throws GameException {
        super("Blazing Charm",
              "A fiery charm that scorches the foe for 25 damage.",
              "Common",
              SingleUseEffectType.DAMAGE,
              25);
    }

    @Override
    public MagicItem copy() {
        try {
            return new BlazingCharm();
        } catch (GameException e) {
            throw new RuntimeException(e);
        }
    }
}
