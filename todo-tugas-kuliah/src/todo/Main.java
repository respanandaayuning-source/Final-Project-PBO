package todo;

import java.io.IOException;
import javax.swing.SwingUtilities;

import todo.service.ReminderService;
import todo.service.TaskManager;
import todo.storage.FileTaskStorage;
import todo.storage.TaskStorage;
import todo.ui.MainFrame;

/** Titik masuk: gabungkan semua komponen. (Anggota 3) */
public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        TaskStorage storage = new FileTaskStorage("data/tasks.csv");

        try {
            manager.setAll(storage.load());
        } catch (IOException e) {
            System.err.println("Gagal memuat data: " + e.getMessage());
        }

        ReminderService reminder = new ReminderService(manager);

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(manager, reminder);
            // TODO (A3): WindowListener -> storage.save(manager.getAll()) saat jendela ditutup
            frame.setVisible(true);
        });
    }
}
