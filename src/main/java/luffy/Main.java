package luffy;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Main JavaFX application class for Luffy GUI. This class extends Application and provides the
 * entry point for the JavaFX GUI version of the Luffy task management application.
 */
public class Main extends Application {

    /**
     * Starts the JavaFX application by setting up the primary stage with a simple "Hello World"
     * label. This is the basic setup following JavaFX Part 1 tutorial.
     *
     * @param stage the primary stage provided by JavaFX
     */
    @Override
    public void start(Stage stage) {
        Label helloWorld = new Label("Hello World!"); // Creating a new Label control
        Scene scene = new Scene(helloWorld); // Setting the scene to be our Label

        stage.setScene(scene); // Setting the stage to show our scene
        stage.show(); // Render the stage.
    }
}
