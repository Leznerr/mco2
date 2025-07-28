package controller;

import model.core.Character;
import model.core.ClassType;
import model.core.RaceType;
import model.core.Ability;
import model.service.ClassService;
import model.util.GameException;
import org.junit.jupiter.api.Test;
import view.BattleView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AbilityDropdownTest {

    private static class CaptureView extends BattleView {
        List<String> p1Options;
        List<String> p2Options;
        CaptureView() { super(1); }
        @Override public void displayBattleStart(Character c1, Character c2) {}
        @Override public void displayTurnResults(model.battle.CombatLog log) {}
        @Override public void displayBattleEnd(Character winner) {}
        @Override
        public void updateAbilityDropdown(int playerID, java.util.List<String> options) {
            super.updateAbilityDropdown(playerID, options);
            if(playerID==1) p1Options = new ArrayList<>(options);
            else p2Options = new ArrayList<>(options);
        }
    }

    @Test
    public void testDropdownRespectsUnlockedSlots() throws Exception {
        List<Ability> all = ClassService.INSTANCE.getAvailableAbilities(ClassType.WARRIOR);
        Character c1 = new Character("A", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        // unlock slots to allow 5 abilities
        c1.setLevel(4);
        c1.unlockAbilitySlot();
        c1.unlockAbilitySlot();
        c1.setAbilities(all);

        // lock to 3 slots via reflection
        Field f = Character.class.getDeclaredField("unlockedAbilitySlots");
        f.setAccessible(true);
        f.setInt(c1, 3);

        Character c2 = new Character("B", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        c2.setAbilities(all.subList(0,3));

        CaptureView view = new CaptureView();
        BattleController bc = new BattleController(view);
        bc.startBattle(c1, c2);

        assertEquals(3, view.p1Options.size());
    }
}
