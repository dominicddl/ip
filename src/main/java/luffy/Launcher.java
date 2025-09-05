package luffy;

import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues with JavaFX. This class serves as the entry point
 * for the JavaFX version of the Luffy application.
 */
public class Launcher {
    /**
     * Main entry point for the JavaFX GUI version of Luffy.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
