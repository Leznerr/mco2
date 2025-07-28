package controller;

import model.core.ClassType;
import model.core.Character;
import model.core.Player;
import model.core.RaceType;
import model.item.PassiveItem;
import model.item.MagicItem;
import model.item.RarityType;
import model.util.GameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for TradeController logic. */
public class TradeControllerTest {

    private Player p1;
    private Player p2;
    private Character c1;
    private Character c2;
    private MagicItem item1;
    private MagicItem item2;
    private TradeController controller;

    @BeforeEach
    public void setup() throws GameException {
        p1 = new Player("Alice");
        p2 = new Player("Bob");
        c1 = new Character("A", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        c2 = new Character("B", RaceType.ELF, ClassType.MAGE, List.of());
        item1 = new PassiveItem("Ring", "", RarityType.COMMON);
        item2 = new PassiveItem("Amulet", "", RarityType.COMMON);
        c1.getInventory().addItem(item1);
        c2.getInventory().addItem(item2);
        p1.addCharacter(c1);
        p2.addCharacter(c2);
        controller = new TradeController(List.of(p1, p2));
    }

    @Test
    public void testSuccessfulTrade() throws GameException {
        controller.executeTrade(c1, List.of(item1), c2, List.of(item2));
        assertTrue(c1.getInventory().getAllItems().contains(item2));
        assertTrue(c2.getInventory().getAllItems().contains(item1));
    }

    @Test
    public void testTradeWithSelfFails() {
        assertThrows(GameException.class, () ->
                controller.executeTrade(c1, List.of(item1), c1, List.of(item1)));
    }

    @Test
    public void testTradeNonOwnedItemFails() {
        PassiveItem other = new PassiveItem("Other", "", RarityType.COMMON);
        assertThrows(GameException.class, () ->
                controller.executeTrade(c1, List.of(other), c2, List.of(item2)));

    }

    @Test
    public void testTradeNoItemsSelectedFails() {
        assertThrows(GameException.class, () ->
                controller.executeTrade(c1, List.of(), c2, List.of()));
    }
}
