package luffy;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Custom control representing a dialog box consisting of an ImageView to represent the speaker's
 * face and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    private Label text;
    private ImageView displayPicture;

    /**
     * Creates a dialog box with the specified text and image.
     *
     * @param text the text to display in the dialog box
     * @param img the image to display as the speaker's avatar
     */
    private DialogBox(String text, Image img) {
        this.text = new Label(text);
        displayPicture = new ImageView(img);

        this.text.setWrapText(true);
        displayPicture.setFitWidth(100.0);
        displayPicture.setFitHeight(100.0);

        this.setAlignment(Pos.TOP_RIGHT);
        this.getChildren().addAll(this.text, displayPicture);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a dialog box for user input.
     *
     * @param text the user's input text
     * @param img the user's avatar image
     * @return a DialogBox representing the user's message
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Creates a dialog box for Luffy's response.
     *
     * @param text Luffy's response text
     * @param img Luffy's avatar image
     * @return a DialogBox representing Luffy's message, with flipped layout
     */
    public static DialogBox getLuffyDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.flip();
        return db;
    }
}
