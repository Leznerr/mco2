package model.item;

import model.battle.CombatLog;
import model.core.ClassType;
import model.core.Character;
import model.core.RaceType;
import model.util.GameException;
import model.util.StatusEffectType;
import model.item.RarityType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MagicItemEffectTest {

    @Test
    public void testPotionOfMinorHealing() throws GameException {
        Character c = new Character("A", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        c.takeDamage(30);
        SingleUseItem item = new SingleUseItem("Potion of Minor Healing", "Heals", RarityType.COMMON,
                SingleUseEffectType.HEAL_HP, 40);
        item.applyEffect(c, new CombatLog());
        assertEquals(c.getMaxHp(), c.getCurrentHp());
    }

    @Test
    public void testScrollOfMinorEnergy() throws GameException {
        Character c = new Character("B", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        c.spendEp(c.getCurrentEp());
        SingleUseItem item = new SingleUseItem("Scroll of Minor Energy", "EP", RarityType.COMMON,
                SingleUseEffectType.RESTORE_EP, 20);
        item.applyEffect(c, new CombatLog());
        assertEquals(20, c.getCurrentEp());
    }

    @Test
    public void testDefendersAegisAppliesImmunity() throws GameException {
        Character c = new Character("C", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        SingleUseItem item = new SingleUseItem("Defender's Aegis", "Immune", RarityType.COMMON,
                SingleUseEffectType.GRANT_IMMUNITY, 1);
        item.applyEffect(c, new CombatLog());
        assertTrue(c.hasStatusEffect(StatusEffectType.IMMUNITY));
    }
}
