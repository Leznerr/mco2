package controller;

import model.core.ClassType;
import model.core.Character;
import model.core.RaceType;
import model.item.PassiveItem;
import model.item.RarityType;
import model.util.GameException;
import model.battle.CombatLog;
import view.BattleView;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PassiveItemEffectTest {

    private static class CaptureView extends BattleView {
        List<String> logEntries;
        CaptureView() { super(1); }
        @Override public void displayBattleStart(Character c1, Character c2) {}
        @Override public void displayTurnResults(CombatLog log) { logEntries = log.getLogEntries(); }
        @Override public void displayBattleEnd(Character winner) {}
    }

    @Test
    public void testAmuletOfVitalityIncreasesMaxHp() throws GameException {
        System.setProperty("java.awt.headless", "true");
        Character c1 = new Character("A", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        Character c2 = new Character("B", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        PassiveItem amulet = new PassiveItem("Amulet of Vitality", "", RarityType.UNCOMMON);
        c1.getInventory().addItem(amulet);
        c1.getInventory().equipItem(amulet);
        int baseHp = c1.getMaxHp();

        CaptureView view = new CaptureView();
        BattleController bc = new BattleController(view);
        bc.startBattle(c1, c2);
        Character battleCopy = bc.getBattleCopy(c1);
        assertEquals(baseHp + 20, battleCopy.getMaxHp());
        assertNotNull(view.logEntries);
        boolean logged = view.logEntries.stream().anyMatch(e -> e.contains("Amulet of Vitality"));
        assertTrue(logged);
    }
}
