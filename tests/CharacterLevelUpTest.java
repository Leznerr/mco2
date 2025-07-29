package tests;

import model.core.Character;
import model.core.ClassType;
import model.core.RaceType;

public class CharacterLevelUpTest {
    public static void main(String[] args) throws Exception {
        Character c = new Character("Hero", RaceType.HUMAN, ClassType.WARRIOR);
        // Simulate 4 recorded wins
        for (int i = 0; i < 4; i++) {
            c.incrementBattlesWon();
        }

        assert c.getLevel() == 1 : "Level should start at 1";
        assert !c.canLevelUp() : "Should not level up before milestone";

        // Record the 5th win and evaluate level up
        c.incrementBattlesWon();
        if (c.canLevelUp()) {
            c.levelUp();
        }

        assert c.getLevel() == 2 : "Level should increase after fifth win";
        assert c.getBattlesWon() == 0 : "Battles won should reset after leveling";
        System.out.println("CharacterLevelUpTest passed");
    }
}
