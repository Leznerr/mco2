package controller;

import model.core.ClassType;
import model.core.Character;
import model.core.RaceType;
import model.item.PassiveItem;
import model.item.SingleUseEffectType;
import model.item.SingleUseItem;
import model.util.GameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for InventoryController */
public class InventoryControllerTest {

    private Character character;
    private InventoryController controller;

    @BeforeEach
    public void setup() throws GameException {
        character = new Character("Hero", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        controller = new InventoryController(character);
    }

    @Test
    public void testEquipAndUnequip() throws GameException {
        PassiveItem item = new PassiveItem("Ring", "Nice ring", "Common");
        character.getInventory().addItem(item);
        controller.handleEquipItemRequest(item);
        assertEquals(item, character.getInventory().getEquippedItem());

        controller.handleUnequipItemRequest();
        assertNull(character.getInventory().getEquippedItem());
    }

    @Test
    public void testUseSingleUseItem() throws GameException {
        SingleUseItem potion = new SingleUseItem("Potion", "Heal", "Common", SingleUseEffectType.HEAL_HP, 10);
        character.getInventory().addItem(potion);
        controller.handleUseItemRequest(potion);
        assertFalse(character.getInventory().getAllItems().contains(potion));
    }
}
