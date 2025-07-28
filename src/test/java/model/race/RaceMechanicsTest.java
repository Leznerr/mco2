package model.race;

import model.battle.AbilityMove;
import model.battle.CombatLog;
import model.core.*;
import model.util.GameException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RaceMechanicsTest {

    @Test
    public void testVampireLifesteal() throws GameException {
        Ability strike = new Ability("Strike","dmg",0, AbilityEffectType.DAMAGE,30, StatusEffectType.NONE);
        Character vamp = new Character("V", RaceType.VAMPIRE, ClassType.WARRIOR, List.of(strike));
        Character target = new Character("T", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        vamp.takeDamage(20); // ensure not full HP
        int beforeHp = vamp.getCurrentHp();
        AbilityMove move = new AbilityMove(strike);
        move.execute(vamp, target, new CombatLog());
        assertEquals(beforeHp + 10, vamp.getCurrentHp());
    }

    @Test
    public void testOrcRage() throws GameException {
        Ability strike = new Ability("Hit","dmg",0, AbilityEffectType.DAMAGE,20, StatusEffectType.NONE);
        Character orc = new Character("O", RaceType.ORC, ClassType.WARRIOR, List.of(strike));
        Character target = new Character("T", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        orc.takeDamage(orc.getMaxHp() - 20); // drop below 30%
        int before = target.getCurrentHp();
        new AbilityMove(strike).execute(orc, target, new CombatLog());
        int dealt = before - target.getCurrentHp();
        assertEquals(30, dealt); // 20 base +10 rage
    }
}
