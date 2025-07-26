package model.item;

import model.service.MagicItemFactory;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MagicItemFactoryTest {
    private static class FixedRandom extends Random {
        private final int[] values;
        private int idx = 0;
        FixedRandom(int... v) { this.values = v; }
        @Override
        public int nextInt(int bound) {
            int val = values[idx++];
            return val % bound;
        }
    }

    @Test
    public void testRarityBoundaries() {
        MagicItem common = MagicItemFactory.createRandomReward(new FixedRandom(10,0));
        assertEquals("Common", common.getRarity());
        MagicItem uncommon = MagicItemFactory.createRandomReward(new FixedRandom(60,0));
        assertEquals("Uncommon", uncommon.getRarity());
        MagicItem rare = MagicItemFactory.createRandomReward(new FixedRandom(98,0));
        assertEquals("Rare", rare.getRarity());
    }
}
