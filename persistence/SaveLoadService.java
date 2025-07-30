package persistence;

import model.core.Player;
import persistence.HallOfFameData;
import model.core.HallOfFameEntry;
import model.util.GameException;
import model.util.Constants;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;

public class SaveLoadService {

    // File paths for saving and loading game data
    private static final String GAME_DATA_FILE = Constants.SAVE_FILE_PATH;
    private static final String HALL_OF_FAME_FILE = Constants.HALL_OF_FAME_SAVE_PATH;

    /**
     * Saves the game data to a designated file.
     *
     * @param gameData the game data object to be saved
     * @throws GameException if an I/O error occurs during saving
     */
    public static void saveGame(GameData gameData) throws GameException {
        Path file = Path.of(GAME_DATA_FILE);
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (ObjectOutputStream gameDataStream = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
                gameDataStream.writeObject(gameData); // Save the entire GameData object
                System.out.println("Game data has been saved successfully.");
            }
        } catch (IOException e) {
            // Log the error and wrap it in a custom exception for further handling
            throw new GameException("Failed to save game data", e);
        }
    }

    /**
     * Loads the game data from a designated file.
     * If no save file is found, a new instance of {@code GameData} is returned.
     *
     * @return the loaded game data, or a new game data object if not found
     * @throws GameException if an error occurs while loading the game data
     */
    public static GameData loadGame() throws GameException {
        GameData result;
        try (ObjectInputStream gameDataStream = new ObjectInputStream(new FileInputStream(GAME_DATA_FILE))) {
            result = (GameData) gameDataStream.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("No saved game found. Returning new game data.");
            result = new GameData();
        } catch (IOException | ClassNotFoundException e) {
            throw new GameException("Failed to load game data", e);
        }
        return result;
    }

    /**
     * Saves the Hall of Fame data to a designated file.
     *
     * @param data the Hall of Fame data object to be saved
     * @throws GameException if an I/O error occurs during saving
     */
    public static void saveHallOfFame(HallOfFameData data) throws GameException {
        Path file = Path.of(HALL_OF_FAME_FILE);
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (ObjectOutputStream hallOfFameStream = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
                hallOfFameStream.writeObject(data);
                System.out.println("Hall of Fame has been saved successfully.");
            }
        } catch (IOException e) {
            throw new GameException("Failed to save Hall of Fame data", e);
        }
    }

    /**
     * Loads the Hall of Fame data from a file.
     * Supports both the new format and a legacy format for backward compatibility.
     *
     * @return the loaded Hall of Fame data
     * @throws GameException if an error occurs while loading or upgrading the data
     */
    public static HallOfFameData loadHallOfFame() throws GameException {
        Path current = Path.of(HALL_OF_FAME_FILE);
        Path legacy = Path.of("model/save/hall_of_fame.dat");
        Path file = Files.exists(current) ? current : legacy;

        HallOfFameData result = null;
        if (!Files.exists(file)) {
            System.out.println("No Hall of Fame found. Returning empty Hall of Fame.");
            result = new HallOfFameData();
        } else {
            try (ObjectInputStream hallOfFameStream = new ObjectInputStream(new FileInputStream(file.toFile()))) {
                Object obj = hallOfFameStream.readObject();
                if (obj instanceof HallOfFameData data) {
                    result = data;
                } else if (obj instanceof List<?> list) {
                    // Legacy format: single list of entries (players only)
                    List<HallOfFameEntry> entries = new ArrayList<>();
                    for (Object o : list) {
                        if (o instanceof HallOfFameEntry e) {
                            entries.add(e);
                        }
                    }
                    HallOfFameData data = new HallOfFameData();
                    data.setPlayers(entries);
                    // Persist upgraded format for next run
                    saveHallOfFame(data);
                    result = data;
                } else {
                    // Unknown data - start fresh
                    result = new HallOfFameData();
                }
            } catch (IOException | ClassNotFoundException e) {
                // Corrupt or unreadable file - ignore and start fresh
                result = new HallOfFameData();
            }
        }
        return result;
    }

    /**
     * Adds a new player to the existing game data and persists the changes.
     *
     * @param player the player to be added
     * @throws GameException if an error occurs while loading or saving game data
     */
    public static void addPlayer(Player player) throws GameException {
        try {
            // Load the current game data
            GameData gameData = loadGame(); // This may throw GameException if something goes wrong

            // Add the new player to the game data using a mutable copy
            List<Player> players = new ArrayList<>(gameData.getAllPlayers());
            players.add(player);
            gameData.setAllPlayers(players);

            // Save the updated game data
            saveGame(gameData); // This may throw GameException if save fails

            System.out.println("Player has been added and game data updated.");
        } catch (GameException e) {
            // Wrap any game-related exception into a custom GameException
            throw new GameException("Failed to add player. " + e.getMessage(), e);
        }
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private SaveLoadService() {
    }
}
