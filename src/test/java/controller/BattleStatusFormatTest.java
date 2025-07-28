package controller;

import model.core.Character;
import model.core.ClassType;
import model.core.RaceType;
import model.util.StatusEffectFactory;
import model.util.StatusEffectType;
import model.util.GameException;
import org.junit.jupiter.api.Test;
import view.BattleView;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BattleStatusFormatTest {
    private static class DummyView extends BattleView {
        DummyView() {
            super(1);
        }
        @Override public void displayBattleStart(Character c1, Character c2) {}
        @Override public void displayTurnResults(model.battle.CombatLog log) {}
        @Override public void displayBattleEnd(Character winner) {}
    }

    @Test
    public void testFormatStatusDeduplicates() throws Exception {
        Character c = new Character("A", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        c.addStatusEffect(StatusEffectFactory.create(StatusEffectType.POISONED));
        c.addStatusEffect(StatusEffectFactory.create(StatusEffectType.POISONED));

        BattleController bc = new BattleController(new DummyView());
        Method m = BattleController.class.getDeclaredMethod("formatStatus", Character.class);
        m.setAccessible(true);
        String result = (String) m.invoke(bc, c);
        assertTrue(result.contains("POISONED"));
        assertFalse(result.contains("POISONED, POISONED"));
    }
}
