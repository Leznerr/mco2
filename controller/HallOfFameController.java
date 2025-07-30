package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import model.core.Character;
import model.core.HallOfFameEntry;
import model.core.Player;
import model.util.GameException;
import model.util.InputValidator;
import persistence.HallOfFameData;
import persistence.SaveLoadService;
import view.HallOfFameCharactersView;
import view.HallOfFameManagementView;
import view.HallOfFamePlayersView;

/**
 * Controller that manages the Hall of Fame leaderboard.
 * Tracks cumulative wins and provides ranking data for display.
 */
public class HallOfFameController implements ActionListener {

    private final HallOfFameManagementView view;
    private final SceneManager sceneManager;
    private List<HallOfFameEntry> playerEntries;
    private List<HallOfFameEntry> characterEntries;

    /**
     * Constructor for HallOfFameController
     */
    public HallOfFameController(HallOfFameManagementView view, SceneManager sceneManager) {
        InputValidator.requireNonNull(view, "view");
        InputValidator.requireNonNull(sceneManager, "sceneManager");
        this.view = view;
        this.sceneManager = sceneManager;

        try {
            HallOfFameData data = SaveLoadService.loadHallOfFame();
            this.playerEntries = new ArrayList<>(data.getPlayers());
            this.characterEntries = new ArrayList<>(data.getCharacters());
        } catch (GameException e) {
            this.playerEntries = new ArrayList<>();
            this.characterEntries = new ArrayList<>();
            JOptionPane.showMessageDialog(view, "Unable to load Hall of Fame: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        this.view.setController(this); // Connect view to this controller
    }

    /** Opens the Top Players subview and displays leaderboard data. */

    /**
     * Opens and displays the Top Players leaderboard subview,
     * sorted by number of wins and XP.
     */
    private void showTopPlayers() {
        HallOfFamePlayersView playersView = new HallOfFamePlayersView();
        bindHallOfFamePlayersView(playersView);
        playersView.setVisible(true);
    }

    /**
     * Opens and displays the Top Characters leaderboard subview,
     * sorted by number of wins and XP.
     */
    private void showTopCharacters() {
        HallOfFameCharactersView charactersView = new HallOfFameCharactersView();
        bindHallOfFameCharactersView(charactersView);
        charactersView.setVisible(true);
    }

    /**
     * Adds a win to the specified player's Hall of Fame record.
     * <p>
     * If the player is not already in the leaderboard, they are added.
     * </p>
     *
     * @param player the player to update
     * @throws GameException if persistence fails
     */
    public void addWinForPlayer(Player player) throws GameException {
        InputValidator.requireNonNull(player, "player");

        HallOfFameEntry existing = playerEntries.stream()
            .filter(e -> e.getName().equals(player.getName()))
            .findFirst()
            .orElse(null);

        if (existing != null) {
            existing.incrementWins();
        } else {
            playerEntries.add(new HallOfFameEntry(player.getName(), 1, 0, System.currentTimeMillis()));
        }

        persistHallOfFame();
    }

    /**
     * Adds a win to the specified character's Hall of Fame record.
     * <p>
     * Updates XP and adds the character if they are not already recorded.
     * </p>
     *
     * @param character the character to update
     * @throws GameException if persistence fails
     */
    public void addWinForCharacter(Character character) throws GameException {
        InputValidator.requireNonNull(character, "character");

        HallOfFameEntry existing = characterEntries.stream()
            .filter(e -> e.getName().equals(character.getName()))
            .findFirst()
            .orElse(null);

        if (existing != null) {
            existing.incrementWins();
            existing.setXp(character.getXp());
        } else {
            characterEntries.add(new HallOfFameEntry(
                    character.getName(), 1, character.getXp(), System.currentTimeMillis()));
        }

        persistHallOfFame();
    }

    /**
     * Returns the top N players ranked by wins and XP.
     *
     * @param count the number of top entries to return
     * @return an unmodifiable list of ranked {@link HallOfFameEntry} instances
     * @throws GameException if the count is invalid
     */
    public List<HallOfFameEntry> getTopPlayersByWins(int count) throws GameException {
        InputValidator.requirePositiveOrZero(count, "count");

        return playerEntries.stream()
            .sorted(Comparator.comparingInt(HallOfFameEntry::getWins)
                    .thenComparingInt(HallOfFameEntry::getXp).reversed())
            .limit(count)
            .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns the top N characters ranked by wins and XP.
     *
     * @param count the number of top entries to return
     * @return an unmodifiable list of ranked {@link HallOfFameEntry} instances
     * @throws GameException if the count is invalid
     */
    public List<HallOfFameEntry> getTopCharactersByWins(int count) throws GameException {
        InputValidator.requirePositiveOrZero(count, "count");

        return characterEntries.stream()
            .sorted(Comparator.comparingInt(HallOfFameEntry::getWins)
                    .thenComparingInt(HallOfFameEntry::getXp).reversed())
            .limit(count)
            .collect(Collectors.toUnmodifiableList());
    }

    /** Returns a defensive copy of player Hall of Fame entries. */
    public List<HallOfFameEntry> getHallOfFame() {
        return new ArrayList<>(playerEntries);
    }

    /** Returns a defensive copy of character Hall of Fame entries. */
    public List<HallOfFameEntry> getHallOfFameCharacters() {
        return new ArrayList<>(characterEntries);
    }

    /**
     * Replaces the current player Hall of Fame list and saves it.
     *
     * @param entries the new list of Hall of Fame entries
     * @throws GameException if persistence fails or input is null
     */
    public void setHallOfFame(List<HallOfFameEntry> entries) throws GameException {
        InputValidator.requireNonNull(entries, "entries");
        this.playerEntries = new ArrayList<>(entries);
        persistHallOfFame();
    }

    /**
     * Writes both player and character Hall of Fame entries to persistent storage.
     *
     * @throws GameException if an error occurs during saving
     */
    private void persistHallOfFame() throws GameException {
        SaveLoadService.saveHallOfFame(new HallOfFameData(playerEntries, characterEntries));
    }

    /**
     * Binds logic to the Top Characters subview, including return action
     * and displaying formatted Hall of Fame entries.
     *
     * @param view the characters leaderboard view
     */
    public void bindHallOfFameCharactersView(HallOfFameCharactersView view) {
        InputValidator.requireNonNull(view, "view");

        view.setActionListener(e -> {
            if (HallOfFameCharactersView.RETURN.equals(e.getActionCommand())) {
                view.dispose();
            }
        });

        try {
            List<HallOfFameEntry> topCharacters = getTopCharactersByWins(10);
            if (topCharacters.isEmpty()) {
                view.updateTopCharactersList("No records yet!");
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < topCharacters.size(); i++) {
                    HallOfFameEntry e = topCharacters.get(i);
                    String date = LocalDateTime.ofInstant(Instant.ofEpochMilli(e.getLastUpdated()),
                            ZoneId.systemDefault()).toLocalDate().toString();
                    sb.append(String.format("%d. %s - Wins: %d, XP: %d, Last Win: %s",
                            i + 1, e.getName(), e.getWins(), e.getXp(), date));
                    if (i < topCharacters.size() - 1) sb.append("\n\n");
                }
                view.updateTopCharactersList(sb.toString());
            }
        } catch (GameException e) {
            view.updateTopCharactersList("Unable to load Hall of Fame.");
        }
    }

    /**
     * Binds logic to the Top Players subview, including return action
     * and displaying formatted Hall of Fame entries.
     *
     * @param view the players leaderboard view
     */
    public void bindHallOfFamePlayersView(HallOfFamePlayersView view) {
        InputValidator.requireNonNull(view, "view");

        view.setActionListener(e -> {
            if (HallOfFamePlayersView.RETURN.equals(e.getActionCommand())) {
                view.dispose();
            }
        });

        try {
            List<HallOfFameEntry> topPlayers = getTopPlayersByWins(10);
            if (topPlayers.isEmpty()) {
                view.updateTopPlayersList("No records yet!");
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < topPlayers.size(); i++) {
                    HallOfFameEntry e = topPlayers.get(i);
                    String date = LocalDateTime.ofInstant(Instant.ofEpochMilli(e.getLastUpdated()),
                            ZoneId.systemDefault()).toLocalDate().toString();
                    sb.append(String.format("%d. %s - Wins: %d, XP: %d, Last Win: %s",
                            i + 1, e.getName(), e.getWins(), e.getXp(), date));
                    if (i < topPlayers.size() - 1) sb.append("\n\n");
                }
                view.updateTopPlayersList(sb.toString());
            }
        } catch (GameException e) {
            view.updateTopPlayersList("Unable to load Hall of Fame.");
        }
    }

    /**
     * Handles button actions from the Hall of Fame management view.
     * <ul>
     *   <li>Shows Top Players or Top Characters view</li>
     *   <li>Returns to main menu</li>
     *   <li>Displays an error for unknown commands</li>
     * </ul>
     *
     * @param e the action event triggered by user interaction
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case HallOfFameManagementView.SHOW_TOP_PLAYERS -> showTopPlayers();
            case HallOfFameManagementView.SHOW_TOP_CHARACTERS -> showTopCharacters();
            case HallOfFameManagementView.RETURN -> sceneManager.showMainMenu();
            default -> view.showErrorMessage("Unknown action: " + command);
        }
    }
}
