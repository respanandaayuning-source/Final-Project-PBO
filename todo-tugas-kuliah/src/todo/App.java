package todo;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;

import todo.service.ReminderService;
import todo.service.TaskManager;
import todo.storage.FileTaskStorage;
import todo.storage.TaskStorage;
import todo.ui.MainFrame;

/** Titik masuk aplikasi JavaFX. */
public class App extends Application {

    private TaskManager manager;
    private TaskStorage storage;
    private ReminderService reminder;

    @Override
    public void start(Stage stage) {
        // Constructor ini sudah memuat data dari file dan menyimpan otomatis tiap perubahan
        storage  = new FileTaskStorage("data/tasks.csv");
        manager  = new TaskManager(storage);
        reminder = new ReminderService(manager);

        MainFrame frame = new MainFrame(manager, reminder);
        frame.show(stage);

        // Simpan sekali lagi saat ditutup (tidak merusak, hanya cadangan)
        stage.setOnCloseRequest(event -> {
            try {
                storage.save(manager.getAll());
            } catch (IOException e) {
                System.err.println("Gagal menyimpan data: " + e.getMessage());
            }
        });
    }
}