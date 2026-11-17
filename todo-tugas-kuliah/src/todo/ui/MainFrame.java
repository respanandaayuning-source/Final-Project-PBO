package todo.ui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import todo.service.ReminderService;
import todo.service.TaskManager;

/** Jendela utama. (Anggota 2) */
public class MainFrame extends JFrame {
    private final TaskManager manager;
    private final ReminderService reminder;

    public MainFrame(TaskManager manager, ReminderService reminder) {
        super("Manajemen Tugas Kuliah");
        this.manager = manager;
        this.reminder = reminder;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 550);
        setLocationRelativeTo(null);

        // TODO (A2): JTable + tombol + filter + pencarian + label statistik + Timer pengingat
        add(new JLabel("Kerangka GUI - TODO (Anggota 2)", SwingConstants.CENTER));
    }
}
