package model.util;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * <h2>DialogUtils</h2>
 *
 * <p>Utility class for standardized Swing dialogs in Fatal Fantasy: Tactics.</p>
 *
 * <p>This class centralizes common pop-up functionality, ensuring consistent UI messaging
 * throughout the application and simplifying Swing usage in controllers and views.</p>
 *
 * <h3>Design & Threading</h3>
 * <ul>
 *   <li><strong>Utility Class</strong>: All methods are static; instantiation is forbidden.</li>
 *   <li><strong>EDT Safe</strong>: All Swing calls are wrapped in {@code SwingUtilities.invokeLater}.</li>
 *   <li><strong>MVC Separation</strong>: Resides in {@code model.util}, avoiding tight coupling to views.</li>
 * </ul>
 */
public final class DialogUtils {

    /**
     * Private constructor to prevent instantiation.
     *
     * @throws UnsupportedOperationException always
     */
    private DialogUtils() {
        throw new UnsupportedOperationException("DialogUtils is a static utility class.");
    }

    /**
     * Shows an informational dialog with the given title and message.
     *
     * @param title   the dialog window title (non-null, non-blank)
     * @param message the message to display (non-null, non-blank)
     */
    public static void showInformationDialog(String title, String message) {
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE)
        );
    }

    /**
     * Shows an error dialog with the given title and message.
     *
     * @param title   the dialog window title (non-null, non-blank)
     * @param message the error message to display (non-null, non-blank)
     */
    public static void showErrorDialog(String title, String message) {
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE)
        );
    }

    /**
     * Shows a confirmation dialog and returns true if the user selects "Yes".
     *
     * @param title   the dialog window title (non-null, non-blank)
     * @param message the confirmation prompt (non-null, non-blank)
     * @return {@code true} if user selects Yes; {@code false} otherwise
     */
    public static boolean showConfirmationDialog(String title, String message) {
        final int[] result = new int[1];
        try {
            SwingUtilities.invokeAndWait(() ->
                result[0] = JOptionPane.showConfirmDialog(
                    null, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
                )
            );
        } catch (Exception e) {
            showErrorDialog("Dialog Error", "Unexpected error while displaying confirmation dialog.");
            return false;
        }
        return result[0] == JOptionPane.YES_OPTION;
    }
}
