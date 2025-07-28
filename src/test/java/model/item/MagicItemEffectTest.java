package model.item;

import model.battle.CombatLog;
import model.core.ClassType;
import model.core.Character;
import model.core.RaceType;
import model.util.GameException;
import model.util.StatusEffectFactory;
import model.util.StatusEffectType;
import model.item.BlazingCharm;
import model.item.ElvenCloak;
import model.item.PhoenixFeather;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MagicItemEffectTest {

    @Test
    public void testPotionOfMinorHealing() throws GameException {
        Character c = new Character("A", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        c.takeDamage(30);
        SingleUseItem item = new SingleUseItem("Potion of Minor Healing", "Heals", "Common",
                SingleUseEffectType.HEAL_HP, 40);
        item.applyEffect(c, new CombatLog());
        assertEquals(c.getMaxHp(), c.getCurrentHp());
    }

    @Test
    public void testScrollOfMinorEnergy() throws GameException {
        Character c = new Character("B", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        c.spendEp(c.getCurrentEp());
        SingleUseItem item = new SingleUseItem("Scroll of Minor Energy", "EP", "Common",
                SingleUseEffectType.RESTORE_EP, 20);
        item.applyEffect(c, new CombatLog());
        assertEquals(20, c.getCurrentEp());
    }

    @Test
    public void testDefendersAegisAppliesImmunity() throws GameException {
        Character c = new Character("C", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        SingleUseItem item = new SingleUseItem("Defender's Aegis", "Immune", "Common",
                SingleUseEffectType.GRANT_IMMUNITY, 1);
        item.applyEffect(c, new CombatLog());
        assertTrue(c.hasStatusEffect(StatusEffectType.IMMUNITY));
    }

    @Test
    public void testBlazingCharmDamage() throws GameException {
        Character user = new Character("U", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        Character target = new Character("T", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        BlazingCharm charm = new BlazingCharm();
        charm.applyEffect(user, target, new CombatLog());
        assertEquals(target.getMaxHp() - 25, target.getCurrentHp());
    }

    @Test
    public void testElvenCloakBlocksFirstStatus() throws GameException {
        Character c = new Character("E", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        ElvenCloak cloak = new ElvenCloak();
        c.getInventory().addItem(cloak);
        c.getInventory().equipItem(cloak);
        c.addStatusEffect(StatusEffectFactory.create(StatusEffectType.STUNNED));
        assertFalse(c.hasStatusEffect(StatusEffectType.STUNNED));
        c.addStatusEffect(StatusEffectFactory.create(StatusEffectType.POISONED));
        assertTrue(c.hasStatusEffect(StatusEffectType.POISONED));
    }

    @Test
    public void testPhoenixFeatherRevive() throws GameException {
        Character c = new Character("P", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        PhoenixFeather feather = new PhoenixFeather();
        c.getInventory().addItem(feather);
        c.getInventory().equipItem(feather);
        c.takeDamage(c.getMaxHp());
        c.checkPhoenixFeather(new CombatLog());
        assertEquals(40, c.getCurrentHp());
        assertFalse(c.getInventory().getAllItems().contains(feather));
    }
}
