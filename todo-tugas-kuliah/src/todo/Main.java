
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
public class Main extends Application {

    private TaskManager manager;
    private TaskStorage storage;
    private ReminderService reminder;

    @Override
    public void start(Stage stage) {

        // Membuat pengelola tugas dan penyimpanan
        manager = new TaskManager();
        storage = new FileTaskStorage("data/tasks.csv");

        // Memuat data tugas dari file
        try {
            manager.setAll(storage.load());
        } catch (IOException e) {
            System.err.println("Gagal memuat data: " + e.getMessage());
        }

        // Membuat layanan pengingat
        reminder = new ReminderService(manager);

        // Membuat jendela utama
        MainFrame frame = new MainFrame(manager, reminder);

        // Menampilkan aplikasi
        frame.show(stage);

        // Menyimpan data ketika aplikasi ditutup
        stage.setOnCloseRequest(event -> {
            try {
                storage.save(manager.getAll());
                System.out.println("Data tugas berhasil disimpan.");
            } catch (IOException e) {
                System.err.println("Gagal menyimpan data: " + e.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
