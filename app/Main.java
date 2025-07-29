package app;

import javax.swing.SwingUtilities;

import controller.SceneManager;

/**
 * Application entry point and centralized shutdown handler.
 */
public final class Main {

    // Previously held a static SceneManager reference; replaced with a
    // local variable in main to avoid mutable static state.

    private Main() {
        // no instances
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SceneManager::new);
    }

    /**
     * Shuts down the application gracefully.
     */
    public static void shutdown() {
        // Application windows dispose themselves; nothing else required.
    }
}
