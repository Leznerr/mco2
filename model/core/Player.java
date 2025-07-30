package model.core;

import model.util.Constants;
import model.util.GameException;
import model.util.InputValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.io.Serializable;

public final class Player implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String name;
    /** Collection of the player's characters. Always initialized. Never null. */
    private List<Character> characters = new ArrayList<>();
    private int cumulativeWins;

    /**
     * Constructs a new player with the given name and an empty character roster.
     *
     * @param name the player's name (must not be blank)
     * @throws GameException if the name is invalid
     */
    public Player(String name) throws GameException {
        InputValidator.requireNonBlank(name, "Player name");
        this.name = name;
        this.cumulativeWins = 0;
    }

    public String getName() { return name; }
    public List<Character> getCharacters() { return Collections.unmodifiableList(characters); }
    public int getCumulativeWins() { return cumulativeWins; }

    /**
     * Adds a character to the player's roster if valid and unique.
     *
     * @param character the character to add (must not be null)
     * @throws GameException if roster is full or character name is not unique
     */
    public void addCharacter(Character character) throws GameException {
        InputValidator.requireNonNull(character, "Character to add");
        if (characters.size() >= Constants.MAX_CHARACTERS_PER_PLAYER) {
            throw new GameException("Roster is full. Cannot add more than " +
                Constants.MAX_CHARACTERS_PER_PLAYER + " characters.");
        }
        if (!isNameUnique(character.getName())) {
            throw new GameException("A character with the name '" + character.getName() + "' already exists.");
        }
        characters.add(character);
    }

    /**
     * Removes a character by name from the player's roster.
     *
     * @param characterName the name of the character to remove (must not be blank)
     * @return true if the character was removed, false otherwise
     */ 
    public boolean removeCharacter(String characterName) {
        InputValidator.requireNonBlank(characterName, "Character name to remove");
        return characters.removeIf(c -> c.getName().equalsIgnoreCase(characterName));
    }

    /**
     * Retrieves a character by name, ignoring case.
     *
     * @param name the name of the character to find (must not be blank)
     * @return an Optional containing the character if found, or empty if not
     */
    public Optional<Character> getCharacter(String name) {
        InputValidator.requireNonBlank(name, "Character name to find");
        return characters.stream()
            .filter(c -> c.getName().equalsIgnoreCase(name))
            .findFirst();
    }

    /**
     * Checks if name is unique 
     * 
     * @param name the name of the character to be checked
     * @return 1 if unique, 0 otherwise
     */
    private boolean isNameUnique(String name) {
        return characters.stream().noneMatch(c -> c.getName().equalsIgnoreCase(name));
    }

    /**
     * Increments the player's cumulative win counter by one.
     */
    public void incrementWins() {
        this.cumulativeWins++;
    }

    /**
     * Returns a string summary of the player including name, character count, and total wins.
     *
     * @return formatted string representation of the player
     */
    @Override
    public String toString() {
        return String.format("Player [name=%s, characters=%d, wins=%d]",
            name, characters.size(), cumulativeWins);
    }

    /**
     * Ensures the character list is properly initialized after deserialization.
     *
     * @param in the input stream for reading the serialized object
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class of a serialized object could not be found
     */
    private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (characters == null) {
            characters = new ArrayList<>();
        }
    }
}
