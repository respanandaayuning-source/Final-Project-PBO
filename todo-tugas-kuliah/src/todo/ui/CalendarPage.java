package todo.ui;

/*
 * KUNCI JAWABAN. Cocokkan dengan nomor TODO di file kerangka:
 *   TODO 1A = select()   TODO 1B = build()   TODO 1C = draw()
 */

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import todo.model.Task;

/**
 * Halaman Kalender: kalender besar (kiri) + daftar tugas pada tanggal terpilih (kanan).
 * Kelas ini juga dipakai di tab "Kalender" pada halaman Semua tugas (build(false) = tanpa judul halaman).
 */
final class CalendarPage {
    private final MainFrame app;
    private YearMonth month = YearMonth.now();          // bulan yang tampil
    private LocalDate selected = LocalDate.now();       // tanggal yang dipilih
    private final StackPane holder = new StackPane();   // wadah isi; diganti isinya setiap kali kalender berubah

    CalendarPage(MainFrame app) { this.app = app; }

    /** Dipanggil dari mini kalender di Dashboard: langsung menuju tanggal tertentu. */
    void select(LocalDate d) {
        selected = d;
        month = YearMonth.from(d);
    }

    /** withHeader=true untuk halaman penuh (ada judul + bisa scroll); false untuk ditempel di halaman lain. */
    Node build(boolean withHeader) {
        draw(withHeader);
        if (!withHeader) return holder;

        ScrollPane sp = new ScrollPane(holder);
        sp.setFitToWidth(true);
        sp.getStyleClass().add("page-scroll");
        return sp;
    }

    /** Menyusun isi kalender. Dipanggil ulang setiap bulan atau tanggal berubah. */
    private void draw(boolean withHeader) {
        VBox page = new VBox(18);
        if (withHeader) {
            page.getStyleClass().add("center-area");
            page.getChildren().add(new VBox(4,
                    Ui.label("Kalender", "page-title"),
                    Ui.label("Lihat deadline tugas berdasarkan tanggal.", "page-sub")));
        }

        // Daftar tugas dikelompokkan per tanggal deadline: {tanggal -> daftar tugas}
        Map<LocalDate, List<Task>> byDate = MonthGrid.byDate(app.getTasks());

        // ---- Kartu kiri: judul bulan + tombol sebelumnya/berikutnya + grid kalender ----
        Button prev = navButton(Ui.LEFT);
        prev.setOnAction(e -> { month = month.minusMonths(1); draw(withHeader); });
        Button next = navButton(Ui.RIGHT);
        next.setOnAction(e -> { month = month.plusMonths(1); draw(withHeader); });
        Label monthTitle = Ui.label(
                month.getMonth().getDisplayName(TextStyle.FULL, Ui.ID) + " " + month.getYear(), "panel-title");
        HBox nav = new HBox(8, monthTitle, Ui.spacer(), prev, next);
        nav.setAlignment(Pos.CENTER_LEFT);

        // Klik sebuah tanggal = pilih tanggal itu, lalu gambar ulang
        VBox calCard = new VBox(14, nav, MonthGrid.build(month, selected, byDate, true, date -> {
            selected = date;
            month = YearMonth.from(date);
            draw(withHeader);
        }));
        calCard.getStyleClass().add("card");
        Ui.grow(calCard);

        // ---- Kartu kanan: tugas pada tanggal terpilih ----
        VBox dayCard = new VBox(12, Ui.label(selected.format(Ui.LONG_DATE), "card-title"));
        dayCard.getStyleClass().add("card");
        dayCard.setPrefWidth(380);
        dayCard.setMinWidth(380);
        dayCard.setMaxWidth(380);

        List<Task> onDay = new ArrayList<>();
        if (byDate.containsKey(selected)) onDay.addAll(byDate.get(selected));
        onDay.sort((a, b) -> a.getDeadline().compareTo(b.getDeadline()));   // jam paling awal di atas

        if (onDay.isEmpty()) dayCard.getChildren().add(Ui.emptyState("Belum ada tugas"));
        for (Task t : onDay) dayCard.getChildren().add(Ui.taskRow(t, app));

        HBox body = new HBox(18, calCard, dayCard);
        body.setAlignment(Pos.TOP_LEFT);
        page.getChildren().add(body);
        holder.getChildren().setAll(page);
    }

    private static Button navButton(String icon) {
        Button b = new Button();
        b.setGraphic(Ui.icon(icon, 16, "icon-dark"));
        b.getStyleClass().add("circle-btn");
        return b;
    }
}
