package persistence;

import model.core.Player;
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

    // Saves the game data to a file
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

    // Loads the game data from a file
    public static GameData loadGame() throws GameException {
        try (ObjectInputStream gameDataStream = new ObjectInputStream(new FileInputStream(GAME_DATA_FILE))) {
            return (GameData) gameDataStream.readObject(); // Read the GameData object
        } catch (FileNotFoundException e) {
            // If the file does not exist (first-time load), return a new empty GameData object
            System.out.println("No saved game found. Returning new game data.");
            return new GameData(); // Returning an empty game data object
        } catch (IOException | ClassNotFoundException e) {
            // Handle any I/O errors or ClassNotFoundException (in case of deserialization issues)
            throw new GameException("Failed to load game data", e);
        }
    }

    // Saves Hall of Fame data
    public static void saveHallOfFame(List<HallOfFameEntry> hallOfFameEntries) throws GameException {
        Path file = Path.of(HALL_OF_FAME_FILE);
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (ObjectOutputStream hallOfFameStream = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
                hallOfFameStream.writeObject(hallOfFameEntries); // Save entries
                System.out.println("Hall of Fame has been saved successfully.");
            }
        } catch (IOException e) {
            // Log the error and wrap it in a custom exception
            throw new GameException("Failed to save Hall of Fame data", e);
        }
    }

    // Loads the Hall of Fame data
    public static List<HallOfFameEntry> loadHallOfFame() throws GameException {
        try (ObjectInputStream hallOfFameStream = new ObjectInputStream(new FileInputStream(HALL_OF_FAME_FILE))) {
            Object obj = hallOfFameStream.readObject();
            if (obj instanceof List<?> rawList) {
                // Defensive copy into mutable list
                List<HallOfFameEntry> entries = new ArrayList<>();
                for (Object o : rawList) {
                    entries.add((HallOfFameEntry) o);
                }
                return entries;
            } else {
                throw new GameException("Invalid Hall of Fame data.");
            }
        } catch (FileNotFoundException e) {
            // If no Hall of Fame data is found, return an empty list
            System.out.println("No Hall of Fame found. Returning empty Hall of Fame.");
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            // Handle any I/O errors or deserialization issues
            throw new GameException("Failed to load Hall of Fame data", e);
        }
    }

    // Adds a new player to the existing game data and saves it
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

    // Private constructor to prevent instantiation
    private SaveLoadService() {
    }
}
