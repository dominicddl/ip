package luffy;

import java.io.File;
import java.io.IOException;
import luffy.task.TaskList;
import luffy.storage.Storage;
import luffy.ui.Ui;
import luffy.parser.Parser;
import luffy.command.Command;
import luffy.exception.LuffyException;

public class Luffy {
    private TaskList tasks;
    private Storage storage;
    private Ui ui;

    public Luffy(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (IOException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine(); // show the divider line ("_______")
                Command c = Parser.parse(fullCommand);
                c.execute(tasks, ui, storage);
                isExit = c.isExit();
            } catch (LuffyException e) {
                ui.showError(e.getMessage());
            } catch (IOException e) {
                ui.showError(
                        "OOPS!!! Something went wrong with file operations: " + e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    public static void main(String[] args) {
        new Luffy("data" + File.separator + "Luffy.txt").run();
    }
}
