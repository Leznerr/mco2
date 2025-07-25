package app;

import javax.swing.SwingUtilities;

import controller.SceneManager;

/**
 * Application entry point and centralized shutdown handler.
 */
public final class Main {

    private static SceneManager sceneManager;

    private Main() {
        // no instances
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> sceneManager = new SceneManager());
    }

    /**
     * Shuts down the application gracefully.
     */
    public static void shutdown() {
        // Any future cleanup logic can be placed here
        System.exit(0);
    }
}
