package tests;

import controller.BattleController;
import model.battle.CombatLog;
import model.core.Character;
import model.core.ClassType;
import model.core.RaceType;
import model.item.PassiveItem;
import model.item.RarityType;
import sun.misc.Unsafe;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PassiveItemStartTurnTest {
    public static void main(String[] args) throws Exception {
        // Obtain an instance of BattleController without calling its constructor
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe u = (Unsafe) f.get(null);
        BattleController bc = (BattleController) u.allocateInstance(BattleController.class);

        Method apply = BattleController.class.getDeclaredMethod(
                "applyPassiveItemEffect", Character.class, PassiveItem.class, CombatLog.class);
        apply.setAccessible(true);

        // Ring of Focus grants +2 EP
        Character c1 = new Character("Mage", RaceType.HUMAN, ClassType.MAGE);
        PassiveItem ring = new PassiveItem("Ring of Focus", "focus", RarityType.UNCOMMON);
        c1.getInventory().addItem(ring);
        c1.getInventory().equipItem(ring);
        int epBefore = c1.getCurrentEp();
        apply.invoke(bc, c1, ring, new CombatLog());
        assert c1.getCurrentEp() == epBefore + 2 : "Ring of Focus should grant 2 EP";

        // Orb of Resilience heals 5 HP
        Character c2 = new Character("Warrior", RaceType.HUMAN, ClassType.WARRIOR);
        PassiveItem orb = new PassiveItem("Orb of Resilience", "orb", RarityType.RARE);
        c2.getInventory().addItem(orb);
        c2.getInventory().equipItem(orb);
        c2.takeDamage(10);
        int hpBeforeHeal = c2.getCurrentHp();
        apply.invoke(bc, c2, orb, new CombatLog());
        assert c2.getCurrentHp() == hpBeforeHeal + 5 : "Orb of Resilience should heal 5 HP";

        // Ancient Tome of Power grants +5 EP
        Character c3 = new Character("Sage", RaceType.HUMAN, ClassType.MAGE);
        PassiveItem tome = new PassiveItem("Ancient Tome of Power", "tome", RarityType.RARE);
        c3.getInventory().addItem(tome);
        c3.getInventory().equipItem(tome);
        int epBeforeTome = c3.getCurrentEp();
        apply.invoke(bc, c3, tome, new CombatLog());
        assert c3.getCurrentEp() == epBeforeTome + 5 : "Ancient Tome of Power should grant 5 EP";

        System.out.println("PassiveItemStartTurnTest passed");
    }
}
