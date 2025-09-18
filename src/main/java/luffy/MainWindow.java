package luffy;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Luffy luffy;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/User.png"));
    private Image luffyImage = new Image(this.getClass().getResourceAsStream("/images/Luffy.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Luffy instance
     */
    public void setLuffy(Luffy l) {
        luffy = l;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Luffy's reply and
     * then appends them to the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = luffy.getResponse(input);

        // Detect if response is an error message
        boolean isError = response.startsWith("OOPS!!!");

        dialogContainer.getChildren().addAll(DialogBox.getUserDialog(input, userImage),
                DialogBox.getLuffyDialog(response, luffyImage, isError));
        userInput.clear();
    }
}
