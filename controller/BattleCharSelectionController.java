package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import model.core.Ability;
import model.core.Character;
import model.core.Player;
import view.BattleCharSelectionView;

/** Controller for selecting a character for battle. */
public class BattleCharSelectionController implements ActionListener {
    private final BattleCharSelectionView view;
    private final Player player;
    private final Consumer<Character> onSelect;
    private final Runnable onReturn;

    public BattleCharSelectionController(BattleCharSelectionView view,
                                         Player player,
                                         Consumer<Character> onSelect,
                                         Runnable onReturn) {
        this.view = view;
        this.player = player;
        this.onSelect = onSelect;
        this.onReturn = onReturn;
        this.view.setActionListener(this);
        refresh();
    }

    /**
     * Refreshes the view with the player's current character options and
     * their corresponding ability summaries.
     * <p>
     * Called during initialization to populate the selection interface.
     * </p>
     */
    private void refresh() {
        List<Character> chars = player.getCharacters();
        String[] options = chars.stream().map(Character::getName).toArray(String[]::new);
        view.setCharacterOptions(options);
        String details;
        if (chars.isEmpty()) {
            details = "No characters available.";
        } else {
            details = chars.stream()
                    .map(c -> c.toString() + "\nAbilities: " +
                            c.getAbilities().stream().map(Ability::getName)
                             .collect(Collectors.joining(", ")))
                    .collect(Collectors.joining("\n\n"));
        }
        view.updateCharacterList(details);
    }

    /**
     * Handles user interactions from the character selection view.
     * <p>
     * Determines the command type and delegates either to the select handler
     * or returns to the previous menu.
     * </p>
     *
     * @param e the action event triggered by the view
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (BattleCharSelectionView.SELECT.equals(cmd)) {
            handleSelect();
        } else if (BattleCharSelectionView.RETURN.equals(cmd)) {
            view.dispose();
            if (onReturn != null) onReturn.run();
        }
    }

    /**
     * Processes the selected character name, confirms the choice,
     * retrieves the character object from the player, and passes it to the consumer.
     * <p>
     * Displays error dialogs if no character is selected or if the character
     * cannot be found.
     * </p>
     */
    private void handleSelect() {
        String name = view.getSelectedCharacter();
        if (name == null) {
            JOptionPane.showMessageDialog(view, "No character selected.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else if (view.confirmCharacterSelection(name)) {
            Character c = player.getCharacter(name).orElse(null);
            if (c == null) {
                JOptionPane.showMessageDialog(view, "Character not found.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                view.dispose();
                if (onSelect != null) {
                    onSelect.accept(c);
                }
            }
        }
    }
}
