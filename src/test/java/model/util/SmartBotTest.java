package model.util;

import model.battle.Move;
import model.battle.Recharge;
import model.battle.AbilityMove;
import model.core.Ability;
import model.core.AbilityEffectType;
import model.core.ClassType;
import model.core.Character;
import model.core.RaceType;
import model.util.StatusEffectType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/** Unit tests for SmartBot decision making. */
public class SmartBotTest {

    private Character buildBot(List<Ability> abilities) throws GameException {
        Character c = new Character("Bot", RaceType.HUMAN, ClassType.WARRIOR, abilities);
        return c;
    }

    private Character buildOpponent() throws GameException {
        return new Character("Opp", RaceType.HUMAN, ClassType.WARRIOR, List.of());
    }

    @Test
    public void testHealWhenLowHp() throws Exception {
        Ability heal = new Ability("Heal", "heal", 5, AbilityEffectType.HEAL, 30, StatusEffectType.NONE);
        Ability atk = new Ability("Hit", "hit", 5, AbilityEffectType.DAMAGE, 20, StatusEffectType.NONE);
        Character bot = buildBot(List.of(heal, atk));
        bot.takeDamage(80); // low HP
        Character opp = buildOpponent();

        SmartBot sb = new SmartBot(new Random(0));
        Move m = sb.decideMove(bot, opp);
        assertTrue(m instanceof AbilityMove);
        assertEquals(AbilityEffectType.HEAL, ((AbilityMove) m).getAbility().getAbilityEffectType());
    }

    @Test
    public void testRechargeWhenNoOptions() throws Exception {
        Ability atk = new Ability("Hit", "hit", 20, AbilityEffectType.DAMAGE, 20, StatusEffectType.NONE);
        Character bot = buildBot(List.of(atk));
        bot.spendEp(bot.getCurrentEp()); // 0 EP
        Character opp = buildOpponent();

        SmartBot sb = new SmartBot(new Random(0));
        Move m = sb.decideMove(bot, opp);
        assertTrue(m instanceof Recharge);
    }

    @Test
    public void testAttackWhenHealthy() throws Exception {
        Ability heal = new Ability("Heal", "heal", 5, AbilityEffectType.HEAL, 30, StatusEffectType.NONE);
        Ability atk = new Ability("Hit", "hit", 5, AbilityEffectType.DAMAGE, 20, StatusEffectType.NONE);
        Character bot = buildBot(List.of(heal, atk));
        Character opp = buildOpponent();

        SmartBot sb = new SmartBot(new Random(0));
        Move m = sb.decideMove(bot, opp);
        assertTrue(m instanceof AbilityMove);
        assertEquals(AbilityEffectType.DAMAGE, ((AbilityMove) m).getAbility().getAbilityEffectType());
    }
}
