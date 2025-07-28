package controller;

import model.core.Character;
import model.core.Player;
import model.util.DialogUtils;
import model.util.GameException;
import model.util.RandomCharacterGenerator;
import model.util.SmartBot;
import view.BattleCharSelectionView;

import java.util.List;
import java.util.Random;

/** Orchestrates character selection and launches battles. */
public class BattleSetupController {
    private final SceneManager sceneManager;
    private final List<Player> players;

    private Character p1Char;
    private Character p2Char;

    public BattleSetupController(SceneManager sceneManager,
                                 List<Player> players) {
        this.sceneManager = sceneManager;
        this.players = players;
    }

    public void startPvP() {
        selectPlayer1ForPvP();
    }

    public void startPvB() {
        selectPlayerForPvB();
    }

    private void selectPlayer1ForPvP() {
        BattleCharSelectionView view = new BattleCharSelectionView(1);
        view.setVisible(true);
        new BattleCharSelectionController(view, players.get(0), c -> {
            p1Char = c;
            selectPlayer2ForPvP();
        }, () -> sceneManager.showBattleModes(players));
    }

    private void selectPlayer2ForPvP() {
        BattleCharSelectionView view = new BattleCharSelectionView(2);
        view.setVisible(true);
        new BattleCharSelectionController(view, players.get(1), c -> {
            p2Char = c;
            launchPvP();
        }, () -> sceneManager.showBattleModes(players));
    }

    private void selectPlayerForPvB() {
        BattleCharSelectionView view = new BattleCharSelectionView(1);
        view.setVisible(true);
        new BattleCharSelectionController(view, players.get(0), c -> {
            p1Char = c;
            launchPvB();
        }, () -> sceneManager.showBattleModes(players));
    }

    private void launchPvP() {
        sceneManager.showPlayerVsPlayerBattle(players.get(0), p1Char, players.get(1), p2Char);
    }

    private void launchPvB() {
        try {
            Character bot = RandomCharacterGenerator.generate("Bot");
            AIController ai = new AIController(new SmartBot(new Random()));
            sceneManager.showPlayerVsBotBattle(players.get(0), p1Char, bot, ai);
        } catch (GameException e) {
            DialogUtils.showErrorDialog("Battle Error", e.getMessage());
        }
    }
}
