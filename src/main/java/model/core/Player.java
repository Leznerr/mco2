package model.core;

import model.util.Constants;
import model.util.GameException;
import model.util.InputValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class Player {
    private final String name;
    private final List<Character> characters;
    private int cumulativeWins;

    public Player(String name) throws GameException {
        InputValidator.requireNonBlank(name, "Player name");
        this.name = name;
        this.characters = new ArrayList<>();
        this.cumulativeWins = 0;
    }

    public String getName() { return name; }
    public List<Character> getCharacters() { return Collections.unmodifiableList(characters); }
    public int getCumulativeWins() { return cumulativeWins; }

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

    public boolean removeCharacter(String characterName) {
        InputValidator.requireNonBlank(characterName, "Character name to remove");
        return characters.removeIf(c -> c.getName().equalsIgnoreCase(characterName));
    }

    public Optional<Character> getCharacter(String name) {
        InputValidator.requireNonBlank(name, "Character name to find");
        return characters.stream()
            .filter(c -> c.getName().equalsIgnoreCase(name))
            .findFirst();
    }

    private boolean isNameUnique(String name) {
        return characters.stream().noneMatch(c -> c.getName().equalsIgnoreCase(name));
    }

    public void incrementWins() {
        this.cumulativeWins++;
    }

    @Override
    public String toString() {
        return String.format("Player [name=%s, characters=%d, wins=%d]",
            name, characters.size(), cumulativeWins);
    }
}
