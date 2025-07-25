package controller;

import model.battle.Defend;
import model.battle.Recharge;
import model.core.ClassType;
import model.core.Character;
import model.core.RaceType;
import model.util.GameException;
import model.battle.CombatLog;
import view.BattleView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Unit tests for BattleController.submitMove */
public class BattleControllerTest {

    private Character c1;
    private Character c2;

    @BeforeEach
    public void setup() throws GameException {
        // simple characters with no abilities
        c1 = new Character("Alice", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        c2 = new Character("Bob", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        System.setProperty("java.awt.headless", "true");
    }

    /**
     * Ensure that submitting moves for both characters triggers turn execution
     * and the view receives a result callback.
     */
    @Test
    public void testSubmitMoveExecutesTurn() throws GameException {
        TestBattleView view = new TestBattleView(c1, c2);
        BattleController controller = new BattleController(view);
        controller.startBattle(c1, c2);

        controller.submitMove(c1, new Defend());
        controller.submitMove(c2, new Recharge());

        assertTrue(view.turnDisplayed, "Turn results should be displayed");
    }

    /** Minimal BattleView stub to capture callbacks. */
    private static class TestBattleView extends BattleView {
        boolean turnDisplayed = false;

        TestBattleView(Character c1, Character c2) {
            super(c1, c2);
        }

        @Override
        public void displayBattleStart(Character c1, Character c2) {
            // no-op
        }

        @Override
        public void displayTurnResults(CombatLog log) {
            turnDisplayed = true;
        }

        @Override
        public void displayBattleEnd(Character winner) {
            // no-op
        }
    }
}
