package luffy;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Luffy using FXML.
 */
public class Main extends Application {

    private Luffy luffy = new Luffy("data" + File.separator + "Luffy.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Luffy");
            fxmlLoader.<MainWindow>getController().setLuffy(luffy);  // inject the Luffy instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
