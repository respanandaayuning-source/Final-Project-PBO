package todo.ui;

/*
 * KUNCI JAWABAN. Cocokkan dengan nomor TODO di file kerangka:
 *   TODO 1A = active()   TODO 1B = matches()   TODO 1C = button()   TODO 1D = changed()
 */

import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import todo.model.Task;

/**
 * Menyimpan keadaan pencarian + filter, dan membuat tombol "Filter".
 * Dipakai bersama oleh Dashboard dan halaman Semua tugas.
 */
final class TaskFilter {
    String keyword = "";            // kata kunci pencarian
    String course;                  // null = semua mata kuliah
    todo.model.Priority priority;   // null = semua prioritas

    /** Apakah ada filter (mata kuliah/prioritas) yang sedang aktif? */
    boolean active() { return course != null || priority != null; }

    /** Apakah tugas ini lolos pencarian dan filter? */
    boolean matches(Task t) {
        // 1. Kata kunci: dicari di judul, mata kuliah, dan catatan (huruf besar/kecil diabaikan)
        String k = keyword.trim().toLowerCase();
        boolean matchesText = k.isEmpty()
                || String.valueOf(t.getTitle()).toLowerCase().contains(k)
                || String.valueOf(t.getCourse()).toLowerCase().contains(k)
                || String.valueOf(t.getNotes()).toLowerCase().contains(k);   // valueOf: aman kalau isinya null

        // 2. Filter mata kuliah dan prioritas (null berarti "semua", jadi selalu lolos)
        boolean matchesCourse = (course == null) || course.equals(t.getCourse());
        boolean matchesPriority = (priority == null) || priority == t.getPriority();

        return matchesText && matchesCourse && matchesPriority;
    }

    /**
     * Membuat tombol "Filter". Kalau diklik muncul menu: Mata kuliah > ..., Prioritas > ..., Reset.
     * onChange = kode yang dijalankan setelah pilihan berubah (biasanya: isi ulang daftar).
     */
    Button button(MainFrame app, Runnable onChange) {
        Button button = new Button(active() ? "Filter •" : "Filter", Ui.icon(Ui.FILTER, 15, "icon-dark"));
        button.getStyleClass().add("pill-btn");

        button.setOnAction(e -> {
            // Submenu Mata kuliah
            Menu courseMenu = new Menu("Mata kuliah");
            MenuItem allCourses = new MenuItem("Semua mata kuliah");
            allCourses.setOnAction(ev -> { course = null; changed(button, onChange); });
            courseMenu.getItems().add(allCourses);
            for (String c : app.getCourses()) {
                MenuItem item = new MenuItem(c);
                item.setOnAction(ev -> { course = c; changed(button, onChange); });
                courseMenu.getItems().add(item);
            }

            // Submenu Prioritas
            Menu priorityMenu = new Menu("Prioritas");
            MenuItem allPriorities = new MenuItem("Semua prioritas");
            allPriorities.setOnAction(ev -> { priority = null; changed(button, onChange); });
            priorityMenu.getItems().add(allPriorities);
            for (todo.model.Priority p : todo.model.Priority.values()) {
                MenuItem item = new MenuItem(p.getLabel());
                item.setOnAction(ev -> { priority = p; changed(button, onChange); });
                priorityMenu.getItems().add(item);
            }

            // Reset semua filter
            MenuItem reset = new MenuItem("Reset filter");
            reset.setOnAction(ev -> { course = null; priority = null; changed(button, onChange); });

            new ContextMenu(courseMenu, priorityMenu, reset).show(button, Side.BOTTOM, 0, 4);
        });
        return button;
    }

    /** Dipanggil setiap pilihan filter berubah: perbarui tulisan tombol, lalu isi ulang daftar. */
    private void changed(Button button, Runnable onChange) {
        button.setText(active() ? "Filter •" : "Filter");
        onChange.run();
    }
}
