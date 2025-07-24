package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.stream.Collectors;

import model.core.Player;
import model.core.HallOfFameEntry;
import model.util.GameException;
import model.util.InputValidator;
import persistence.SaveLoadService;
import view.HallOfFameCharactersView;
import view.HallOfFamePlayersView;
import view.HallOfFameManagementView;

/**
 * Controller that manages the Hall of Fame leaderboard.
 * Tracks cumulative wins and provides ranking data for display.
 */
public class HallOfFameController implements ActionListener {

    private final HallOfFameManagementView view;
    private List<HallOfFameEntry> hallOfFameEntries;

    public HallOfFameController(HallOfFameManagementView view) {
        InputValidator.requireNonNull(view, "view");
        this.view = view;

        try {
            this.hallOfFameEntries = SaveLoadService.loadHallOfFame();
        } catch (GameException e) {
            this.hallOfFameEntries = new ArrayList<>();
        }

        this.view.setController(this); // Connect view to this controller
    }

    /** Opens the Top Players subview and displays leaderboard data. */
    private void showTopPlayers() {
        HallOfFamePlayersView playersView = new HallOfFamePlayersView();
        bindHallOfFamePlayersView(playersView);
        playersView.setVisible(true);
    }

    /** Opens the Top Characters subview and displays leaderboard data. */
    private void showTopCharacters() {
        HallOfFameCharactersView charactersView = new HallOfFameCharactersView();
        bindHallOfFameCharactersView(charactersView);
        charactersView.setVisible(true);
    }

    /** Adds a win to the specified player in the Hall of Fame. */
    public void addWinForPlayer(Player player) throws GameException {
        InputValidator.requireNonNull(player, "player");

        HallOfFameEntry existing = hallOfFameEntries.stream()
            .filter(e -> e.getPlayerName().equals(player.getName()))
            .findFirst()
            .orElse(null);

        if (existing != null) {
            existing.incrementWins();
        } else {
            hallOfFameEntries.add(new HallOfFameEntry(player.getName(), 1));
        }

        persistHallOfFame();
    }

    /** Returns a ranked, immutable list of top players by wins. */
    public List<HallOfFameEntry> getTopPlayersByWins(int count) throws GameException {
        InputValidator.requirePositiveOrZero(count, "count");

        return hallOfFameEntries.stream()
            .sorted(Comparator.comparingInt(HallOfFameEntry::getWins).reversed())
            .limit(count)
            .collect(Collectors.toUnmodifiableList());
    }

    /** Returns a defensive copy of all Hall of Fame entries. */
    public List<HallOfFameEntry> getHallOfFame() {
        return new ArrayList<>(hallOfFameEntries);
    }

    /** Replaces the entire Hall of Fame list with new entries and persists them. */
    public void setHallOfFame(List<HallOfFameEntry> entries) throws GameException {
        InputValidator.requireNonNull(entries, "entries");
        this.hallOfFameEntries = new ArrayList<>(entries);
        persistHallOfFame();
    }

    /** Writes the Hall of Fame entries to disk. */
    private void persistHallOfFame() throws GameException {
        SaveLoadService.saveHallOfFame(hallOfFameEntries);
    }

    /** Binds return logic and data loading for the Top Characters view. */
    public void bindHallOfFameCharactersView(HallOfFameCharactersView view) {
        InputValidator.requireNonNull(view, "view");

        view.setActionListener(e -> {
            if (HallOfFameCharactersView.RETURN.equals(e.getActionCommand())) {
                view.dispose();
            }
        });

        try {
            List<HallOfFameEntry> topPlayers = getTopPlayersByWins(10);
            if (topPlayers.isEmpty()) {
                view.updateTopCharactersList("No top players yet.");
            } else {
                String content = topPlayers.stream()
                        .map(HallOfFameEntry::toString)
                        .collect(Collectors.joining("\n\n"));
                view.updateTopCharactersList(content);
            }
        } catch (GameException e) {
            view.updateTopCharactersList("Unable to load Hall of Fame.");
        }
    }

    /** Binds return logic and data loading for the Top Players view. */
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
                view.updateTopPlayersList("No top players yet.");
            } else {
                String content = topPlayers.stream()
                        .map(HallOfFameEntry::toString)
                        .collect(Collectors.joining("\n\n"));
                view.updateTopPlayersList(content);
            }
        } catch (GameException e) {
            view.updateTopPlayersList("Unable to load Hall of Fame.");
        }
    }

    /** Handles all button clicks from the Hall of Fame management view. */
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case HallOfFameManagementView.SHOW_TOP_PLAYERS -> showTopPlayers();
            case HallOfFameManagementView.SHOW_TOP_CHARACTERS -> showTopCharacters();
            case HallOfFameManagementView.RETURN -> view.dispose();
            default -> view.showErrorMessage("Unknown action: " + command);
        }
    }
}
