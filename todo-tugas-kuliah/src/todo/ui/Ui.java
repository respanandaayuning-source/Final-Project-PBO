package todo.ui;

/*
 * KUNCI JAWABAN. Cocokkan dengan nomor TODO di file kerangka:
 *   TODO 1A-1D = label(), spacer(), grow(), vgrow()
 *   TODO 2A-2E = deadlineText(), relativeText(), priorityBadge(), emptyState(), taskRow()
 *   TODO 3A-3B = moreButton(), taskMenu()
 *   TODO 4A-4B = stageOf(), colorIndex()
 *   TODO 5A    = icon()
 */

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

import todo.model.Task;

/**
 * "Kotak perkakas" yang dipakai semua halaman. Isinya hanya method static, jadi dipanggil Ui.nama(...).
 *   - ikon     : Ui.icon(Ui.PLUS, 16, "icon-white")   (path SVG digambar sebagai garis)
 *   - teks     : Ui.label("teks", "nama-class-css")
 *   - tata     : Ui.spacer() (ruang elastis), Ui.grow(node) (node mengisi sisa lebar)
 *   - tugas    : Ui.deadlineText / Ui.relativeText (format teks), Ui.taskRow (satu baris tugas)
 */
final class Ui {
    private Ui() {}

    static final Locale ID = new Locale("id", "ID");
    static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH.mm");
    static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("d MMM", ID);
    static final DateTimeFormatter LONG_DATE = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", ID);

    /** Lokasi style.css; diisi MainFrame agar dialog ikut memakainya. */
    static String cssUrl;

    // Warna titik mata kuliah (urutan sama dengan kartu chip-c0..c3 di CSS)
    static final String[] DOT_COLORS = {"#FFD21F", "#A9C77B", "#C0B6AC", "#D2BFA8"};

    // ---- Ikon (gaya garis, kotak 24x24) ----
    static final String GRID = "M3 3h7v7H3z M14 3h7v7h-7z M14 14h7v7h-7z M3 14h7v7H3z";
    static final String LIST = "M8 6h13 M8 12h13 M8 18h13 M3 6h.01 M3 12h.01 M3 18h.01";
    static final String CALENDAR = "M8 2v4 M16 2v4 M3 10h18 M5 4h14a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2z";
    static final String CHART = "M12 20V10 M18 20V4 M6 20v-4";
    static final String SEARCH = "M11 19a8 8 0 1 0 0-16 8 8 0 0 0 0 16z M21 21l-4.3-4.3";
    static final String PLUS = "M12 5v14 M5 12h14";
    static final String CLOCK = "M12 22a10 10 0 1 0 0-20 10 10 0 0 0 0 20z M12 6v6l4 2";
    static final String BELL = "M18 8a6 6 0 0 0-12 0c0 7-3 9-3 9h18s-3-2-3-9 M13.7 21a2 2 0 0 1-3.4 0";
    static final String LEFT = "M15 18l-6-6 6-6";
    static final String RIGHT = "M9 18l6-6-6-6";
    static final String DOWN = "M6 9l6 6 6-6";
    static final String CHECK = "M20 6L9 17l-5-5";
    static final String ARROW = "M7 17L17 7 M7 7h10v10";
    static final String FILTER = "M3 6h18 M7 12h10 M10 18h4";
    static final String SORT = "M3 6h13 M3 12h9 M3 18h5 M17 10v10 M14 17l3 3 3-3";
    static final String BOARD = "M3 5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z M9 3v18 M15 3v18";
    static final String DOTS = "M5 12h.01 M12 12h.01 M19 12h.01";
    static final String MENU = "M3 6h18 M3 12h18 M3 18h18";   // ikon tombol buka/tutup sidebar
    static final String BOOK = "M2 4h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z M22 4h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z";

    /** Membuat ikon dari path SVG. Warna diatur lewat CSS (class "icon" + styleClass). */
    static Node icon(String path, double size, String styleClass) {
        SVGPath p = new SVGPath();
        p.setContent(path);
        p.getStyleClass().add("icon");
        if (styleClass != null) p.getStyleClass().add(styleClass);
        double s = size / 24.0;
        p.setScaleX(s);
        p.setScaleY(s);
        // Kotak berukuran tetap supaya semua ikon sejajar walau bentuk path-nya berbeda
        StackPane box = new StackPane(new Group(p));
        box.setMinSize(size, size);
        box.setPrefSize(size, size);
        box.setMaxSize(size, size);
        return box;
    }

    static Label label(String text, String... styleClasses) {
        Label l = new Label(text);
        l.getStyleClass().addAll(styleClasses);
        return l;
    }

    static Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, javafx.scene.layout.Priority.ALWAYS);
        return r;
    }

    static <T extends Node> T grow(T n) {
        HBox.setHgrow(n, javafx.scene.layout.Priority.ALWAYS);
        return n;
    }

    static <T extends Node> T vgrow(T n) {
        VBox.setVgrow(n, javafx.scene.layout.Priority.ALWAYS);
        return n;
    }

    // ---- Status tugas untuk papan ----
    enum Phase { TODO, DOING, DONE, LATE }

    /** Belum mulai / Dikerjakan (deadline hari ini atau besok) / Selesai / Terlambat. */
    static Phase stageOf(Task t) {
        if (t.isDone()) return Phase.DONE;
        if (t.isOverdue()) return Phase.LATE;
        LocalDate d = t.getDeadline().toLocalDate();
        return d.isAfter(LocalDate.now().plusDays(1)) ? Phase.TODO : Phase.DOING;
    }

    static int colorIndex(String course, List<String> courses) {
        int i = courses.indexOf(course);
        return i < 0 ? 0 : i % DOT_COLORS.length;
    }

    // ---- Format teks ----
    /** "Hari ini · 23.59", "Besok · 17.00", atau "25 Nov · 23.59". */
    static String deadlineText(Task t) {
        LocalDateTime dl = t.getDeadline();
        LocalDate d = dl.toLocalDate();
        LocalDate today = LocalDate.now();
        String day = d.equals(today) ? "Hari ini" : d.equals(today.plusDays(1)) ? "Besok" : d.format(DAY);
        return day + " · " + dl.format(TIME);
    }

    /** "2 jam lagi", "Besok", "Lewat 1 hari", "Selesai". */
    static String relativeText(Task t) {
        if (t.isDone()) return "Selesai";
        LocalDateTime now = LocalDateTime.now();
        Duration diff = Duration.between(now, t.getDeadline());
        if (t.isOverdue()) {
            Duration late = diff.negated();
            if (late.toDays() >= 1) return "Lewat " + late.toDays() + " hari";
            if (late.toHours() >= 1) return "Lewat " + late.toHours() + " jam";
            return "Lewat " + Math.max(1, late.toMinutes()) + " menit";
        }
        LocalDate d = t.getDeadline().toLocalDate();
        LocalDate today = LocalDate.now();
        if (d.equals(today)) {
            if (diff.toHours() >= 1) return diff.toHours() + " jam lagi";
            return Math.max(1, diff.toMinutes()) + " menit lagi";
        }
        if (d.equals(today.plusDays(1))) return "Besok";
        return java.time.temporal.ChronoUnit.DAYS.between(today, d) + " hari lagi";
    }

    static Label priorityBadge(todo.model.Priority p) {
        String cls;
        switch (p) {
            case HIGHT: cls = "prio-high"; break;
            case LOW:   cls = "prio-low";  break;
            default:    cls = "prio-med";
        }
        Label l = label(p.getLabel(), "badge", cls);
        l.setMinWidth(Region.USE_PREF_SIZE);   // badge tidak ikut menyusut jadi "..."
        return l;
    }

    static Node emptyState(String text) {
        Label l = label(text, "empty-text");
        VBox box = new VBox(l);
        box.setAlignment(Pos.CENTER);
        box.setMinHeight(90);
        return box;
    }

    // ---- Baris tugas (dipakai Dashboard, daftar, dan kalender) ----
    static Node taskRow(Task t, MainFrame app) {
        CheckBox cb = new CheckBox();
        cb.getStyleClass().add("round-check");
        cb.setSelected(t.isDone());
        cb.setOnAction(e -> app.toggle(t));

        Label title = label(t.getTitle(), "row-title");
        if (t.isDone()) title.getStyleClass().add("done-text");

        Label when = label(deadlineText(t), "row-meta");
        if (t.isOverdue()) when.getStyleClass().add("late-text");
        HBox clock = new HBox(5, icon(CLOCK, 13, "icon-muted"), when);
        clock.setAlignment(Pos.CENTER_LEFT);
        clock.setMinWidth(Region.USE_PREF_SIZE);
        Label courseLabel = label(t.getCourse(), "row-meta");
        courseLabel.setMinWidth(0);
        HBox meta = new HBox(14, courseLabel, clock);
        meta.setAlignment(Pos.CENTER_LEFT);
        title.setMinWidth(0);
        VBox text = new VBox(5, title, meta);
        text.setMinWidth(0);

        HBox row = new HBox(14, cb, text, spacer(), priorityBadge(t.getPriority()), moreButton(t, app));
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("task-row");
        row.setOnMouseClicked(e -> { if (e.getClickCount() == 2) app.openEdit(t); });
        return row;
    }

    static Button moreButton(Task t, MainFrame app) {
        Button b = new Button();
        b.setGraphic(icon(DOTS, 20, "icon-muted"));
        b.getStyleClass().add("icon-btn");
        b.setOnAction(e -> taskMenu(t, app).show(b, Side.BOTTOM, 0, 4));
        return b;
    }

    static ContextMenu taskMenu(Task t, MainFrame app) {
        MenuItem edit = new MenuItem("Ubah");
        edit.setOnAction(e -> app.openEdit(t));
        MenuItem done = new MenuItem(t.isDone() ? "Tandai belum selesai" : "Tandai selesai");
        done.setOnAction(e -> app.toggle(t));
        MenuItem del = new MenuItem("Hapus");
        del.setOnAction(e -> app.delete(t));
        return new ContextMenu(edit, done, del);
    }
}
