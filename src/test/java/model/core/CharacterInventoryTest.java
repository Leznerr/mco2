package model.core;

import model.item.PassiveItem;
import model.item.RarityType;
import model.item.SingleUseEffectType;
import model.item.SingleUseItem;
import model.util.GameException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Character} inventory delegation methods.
 */
public class CharacterInventoryTest {

    @Test
    public void testEquipAndUnequipDelegation() throws GameException {
        Character c = new Character("Hero", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        PassiveItem ring = new PassiveItem("Ring", "", RarityType.COMMON);
        c.getInventory().addItem(ring);

        c.equipItem(ring);
        assertEquals(ring, c.getEquippedItem());

        c.unequipItem();
        assertNull(c.getEquippedItem());
    }

    @Test
    public void testHasItemAndUseSingleUseItem() throws GameException {
        Character c = new Character("Hero", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        SingleUseItem potion = new SingleUseItem("Potion", "", RarityType.COMMON, SingleUseEffectType.HEAL_HP, 10);
        c.getInventory().addItem(potion);

        assertTrue(c.hasItem(potion));
        c.useSingleUseItem(potion);
        assertFalse(c.hasItem(potion));
    }
}
