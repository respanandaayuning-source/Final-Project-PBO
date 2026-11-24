package todo.ui;

/*
 * KUNCI JAWABAN. Cocokkan dengan nomor TODO di file kerangka:
 *   TODO 1A-1F = semuanya ada di build() (dengan pembantu smallCard(), row(), bar())
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import todo.model.Task;
import todo.service.TaskStatistics;

/** Halaman Statistik: persentase selesai, tiga kartu angka, progres per mata kuliah dan per prioritas. */
final class StatsPage {
    private final MainFrame app;

    StatsPage(MainFrame app) { this.app = app; }

    Node build() {
        List<Task> all = app.getTasks();

        // Angka-angka dihitung oleh TaskStatistics (buatan Anggota 1)
        int total = all.size();
        int done = TaskStatistics.countDone(all);
        int pending = TaskStatistics.countPending(all);
        int late = TaskStatistics.countOverdue(all);
        double ratio = (total == 0) ? 0 : (double) done / total;     // 0.0 sampai 1.0

        // --- Baris atas: kartu persentase + tiga kartu angka ---
        VBox percentCard = new VBox(8,
                Ui.label(Math.round(ratio * 100) + "%", "big-percent"),
                Ui.label("Penyelesaian tugas  ·  " + done + " dari " + total, "stat-label"),
                bar(ratio));
        percentCard.getStyleClass().add("card");
        Ui.grow(percentCard);
        percentCard.setPrefWidth(10);

        HBox top = new HBox(16, percentCard,
                smallCard(done, "Selesai", "stat-card-done"),
                smallCard(pending, "Tertunda", null),
                smallCard(late, "Terlambat", "stat-card-late"));

        // --- Progres per mata kuliah ---
        VBox perCourse = new VBox(14, Ui.label("Progres per mata kuliah", "card-title"));
        perCourse.getStyleClass().add("card");

        // Kelompokkan tugas per mata kuliah (TreeMap = otomatis urut abjad)
        Map<String, List<Task>> byCourse = new TreeMap<>();
        for (Task t : all) {
            if (!byCourse.containsKey(t.getCourse())) byCourse.put(t.getCourse(), new ArrayList<>());
            byCourse.get(t.getCourse()).add(t);
        }
        if (byCourse.isEmpty()) perCourse.getChildren().add(Ui.emptyState("Belum ada tugas"));
        for (String course : byCourse.keySet()) {
            List<Task> list = byCourse.get(course);
            int doneInCourse = 0;
            for (Task t : list) {
                if (t.isDone()) doneInCourse++;
            }
            perCourse.getChildren().add(row(course, (double) doneInCourse / list.size(),
                    doneInCourse + "/" + list.size()));
        }
        Ui.grow(perCourse);
        perCourse.setPrefWidth(10);

        // --- Tugas yang belum selesai, per prioritas ---
        VBox perPriority = new VBox(14, Ui.label("Tugas tertunda per prioritas", "card-title"));
        perPriority.getStyleClass().add("card");
        for (todo.model.Priority p : todo.model.Priority.values()) {
            int count = 0;
            for (Task t : all) {
                if (!t.isDone() && t.getPriority() == p) count++;
            }
            double share = (pending == 0) ? 0 : (double) count / pending;
            perPriority.getChildren().add(row(p.getLabel(), share, String.valueOf(count)));
        }
        Ui.grow(perPriority);
        perPriority.setPrefWidth(10);

        VBox page = new VBox(18,
                new VBox(4, Ui.label("Statistik", "page-title"),
                            Ui.label("Perkembangan pengerjaan tugas kuliahmu.", "page-sub")),
                top,
                new HBox(18, perCourse, perPriority));
        page.getStyleClass().add("center-area");

        ScrollPane sp = new ScrollPane(page);
        sp.setFitToWidth(true);
        sp.getStyleClass().add("page-scroll");
        return sp;
    }

    /** Kartu angka kecil di kanan atas. */
    private Node smallCard(int number, String label, String styleClass) {
        VBox card = new VBox(2, Ui.label(String.valueOf(number), "stat-number"), Ui.label(label, "stat-label"));
        card.getStyleClass().add("stat-card");
        if (styleClass != null) card.getStyleClass().add(styleClass);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMinWidth(130);
        return card;
    }

    /** Satu baris: nama (kiri), progress bar (tengah), teks (kanan). */
    private Node row(String name, double ratio, String rightText) {
        Label nameLabel = Ui.label(name, "row-title");
        nameLabel.setMinWidth(150);
        nameLabel.setPrefWidth(150);
        ProgressBar progress = bar(ratio);
        Ui.grow(progress);
        HBox row = new HBox(12, nameLabel, progress, Ui.label(rightText, "row-meta"));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private static ProgressBar bar(double ratio) {
        ProgressBar b = new ProgressBar(ratio);
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }
}
