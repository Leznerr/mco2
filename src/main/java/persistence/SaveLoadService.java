package persistence;

import model.core.Player;
import model.core.HallOfFameEntry;
import model.util.GameException;
import java.io.*;
import java.util.List;

public class SaveLoadService {

    // File paths for saving and loading game data
    private static final String GAME_DATA_FILE = "game_data.dat"; 
    private static final String HALL_OF_FAME_FILE = "hall_of_fame.dat"; 

    // Saves the game data to a file
    public static void saveGame(GameData gameData) throws GameException {
        try (ObjectOutputStream gameDataStream = new ObjectOutputStream(new FileOutputStream(GAME_DATA_FILE))) {
            gameDataStream.writeObject(gameData); // Save the entire GameData object
            System.out.println("Game data has been saved successfully.");
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
        try (ObjectOutputStream hallOfFameStream = new ObjectOutputStream(new FileOutputStream(HALL_OF_FAME_FILE))) {
            hallOfFameStream.writeObject(hallOfFameEntries); // Save the Hall of Fame entries
            System.out.println("Hall of Fame has been saved successfully.");
        } catch (IOException e) {
            // Log the error and wrap it in a custom exception
            throw new GameException("Failed to save Hall of Fame data", e);
        }
    }

    // Loads the Hall of Fame data
    public static List<HallOfFameEntry> loadHallOfFame() throws GameException {
        try (ObjectInputStream hallOfFameStream = new ObjectInputStream(new FileInputStream(HALL_OF_FAME_FILE))) {
            Object obj = hallOfFameStream.readObject();
            if (obj instanceof List<?>) {
                List<?> rawList = (List<?>) obj;
                // Ensure the list contains the right type
                return (List<HallOfFameEntry>) rawList;
            } else {
                throw new GameException("Invalid Hall of Fame data.");
            }
        } catch (FileNotFoundException e) {
            // If no Hall of Fame data is found, return an empty list
            System.out.println("No Hall of Fame found. Returning empty Hall of Fame.");
            return List.of(); // Returning an empty list
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

            // Add the new player to the game data
            gameData.getAllPlayers().add(player);

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
