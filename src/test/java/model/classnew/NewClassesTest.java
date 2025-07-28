package model.classnew;

import model.battle.AbilityMove;
import model.battle.CombatLog;
import model.core.*;
import model.service.ClassService;
import model.util.GameException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NewClassesTest {

    @Test
    public void testSummonerAbilitiesRegistered() throws GameException {
        List<Ability> list = ClassService.INSTANCE.getAvailableAbilities(ClassType.SUMMONER);
        assertEquals(5, list.size());
    }

    @Test
    public void testEngineerAbilitiesRegistered() throws GameException {
        List<Ability> list = ClassService.INSTANCE.getAvailableAbilities(ClassType.ENGINEER);
        assertEquals(5, list.size());
    }

    @Test
    public void testSummonSpiritWolfDrainsEp() throws GameException {
        Ability wolf = ClassService.INSTANCE.getAvailableAbilities(ClassType.SUMMONER).get(0);
        Character s = new Character("S", RaceType.HUMAN, ClassType.SUMMONER, List.of(wolf));
        Character t = new Character("T", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        int before = t.getCurrentEp();
        new AbilityMove(wolf).execute(s, t, new CombatLog());
        assertEquals(before - 2, t.getCurrentEp());
    }

    @Test
    public void testProtectiveWispShields() throws GameException {
        Ability shield = ClassService.INSTANCE.getAvailableAbilities(ClassType.SUMMONER).get(2);
        Character s = new Character("S", RaceType.HUMAN, ClassType.SUMMONER, List.of(shield));
        Character t = new Character("T", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        new AbilityMove(shield).execute(s, t, new CombatLog());
        int before = s.getCurrentHp();
        s.takeDamage(20);
        assertEquals(before - 5, s.getCurrentHp());
    }

    @Test
    public void testOverclockSelfDamage() throws GameException {
        Ability over = ClassService.INSTANCE.getAvailableAbilities(ClassType.ENGINEER).get(4);
        Character e = new Character("E", RaceType.HUMAN, ClassType.ENGINEER, List.of(over));
        Character t = new Character("T", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        int before = e.getCurrentHp();
        new AbilityMove(over).execute(e, t, new CombatLog());
        assertEquals(before - 6, e.getCurrentHp());
    }
}
