package todo.ui;

/*
 * KUNCI JAWABAN. Cocokkan dengan nomor TODO di file kerangka:
 *   TODO 1A = byDate()   TODO 1B = build()   TODO 1C = smallCell()   TODO 1D = bigCell()
 */

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import todo.model.Task;

/**
 * Membuat grid kalender bulanan (kolom Senin sampai Minggu, selalu 6 baris).
 * Ada dua versi: kecil (large=false, untuk Dashboard: angka + titik) dan besar (large=true, halaman Kalender: angka + judul tugas).
 * Hari dari bulan sebelum/sesudahnya ikut tampil dengan warna pudar supaya baris selalu penuh.
 */
final class MonthGrid {
    private static final String[] DAYS = {"Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"};

    private MonthGrid() {}

    /** Mengelompokkan tugas per tanggal deadline: {tanggal -> daftar tugas pada tanggal itu}. */
    static Map<LocalDate, List<Task>> byDate(List<Task> tasks) {
        Map<LocalDate, List<Task>> map = new HashMap<>();
        for (Task t : tasks) {
            LocalDate day = t.getDeadline().toLocalDate();
            if (!map.containsKey(day)) map.put(day, new ArrayList<>());
            map.get(day).add(t);
        }
        return map;
    }

    /** onPick = kode yang dijalankan saat sebuah tanggal diklik (menerima tanggal yang diklik). */
    static Node build(YearMonth ym, LocalDate selected, Map<LocalDate, List<Task>> byDate,
                      boolean large, Consumer<LocalDate> onPick) {
        GridPane g = new GridPane();
        g.setHgap(large ? 6 : 2);
        g.setVgap(large ? 6 : 2);
        for (int c = 0; c < 7; c++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / 7);
            g.getColumnConstraints().add(cc);
        }
        for (int c = 0; c < 7; c++) {
            Label h = Ui.label(DAYS[c], "cal-head");
            h.setMaxWidth(Double.MAX_VALUE);
            h.setAlignment(Pos.CENTER);
            g.add(h, c, 0);
        }

        LocalDate first = ym.atDay(1);
        LocalDate start = first.minusDays(first.getDayOfWeek().getValue() - 1);   // Senin pertama
        for (int i = 0; i < 42; i++) {
            LocalDate d = start.plusDays(i);
            List<Task> ts = byDate.get(d);
            Node cell = large ? bigCell(d, ym, selected, ts) : smallCell(d, ym, selected, ts);
            cell.setOnMouseClicked(e -> onPick.accept(d));
            g.add(cell, i % 7, i / 7 + 1);
        }
        return g;
    }

    private static Node smallCell(LocalDate d, YearMonth ym, LocalDate selected, List<Task> ts) {
        Label num = Ui.label(String.valueOf(d.getDayOfMonth()), "cal-num");
        num.setMinSize(32, 32);
        num.setPrefSize(32, 32);
        num.setAlignment(Pos.CENTER);
        if (!YearMonth.from(d).equals(ym)) num.getStyleClass().add("cal-other");
        if (d.equals(LocalDate.now())) num.getStyleClass().add("cal-today");
        else if (d.equals(selected)) num.getStyleClass().add("cal-selected");

        Circle dot = new Circle(2.5);
        dot.getStyleClass().add("cal-dot");
        dot.setVisible(ts != null && !ts.isEmpty());

        VBox cell = new VBox(2, num, dot);
        cell.setAlignment(Pos.CENTER);
        cell.getStyleClass().add("cal-cell");
        return cell;
    }

    private static Node bigCell(LocalDate d, YearMonth ym, LocalDate selected, List<Task> ts) {
        Label num = Ui.label(String.valueOf(d.getDayOfMonth()), "cal-big-num");
        num.setMinSize(26, 26);
        num.setAlignment(Pos.CENTER);
        if (d.equals(LocalDate.now())) num.getStyleClass().add("cal-today");

        VBox cell = new VBox(4, num);
        cell.getStyleClass().add("cal-big");
        cell.setMinHeight(84);
        if (!YearMonth.from(d).equals(ym)) cell.getStyleClass().add("cal-big-other");
        if (d.equals(selected)) cell.getStyleClass().add("cal-big-selected");

        if (ts != null) {
            int shown = 0;
            for (Task t : ts) {
                if (shown == 2) break;
                Label chip = Ui.label(t.getTitle(), "cal-chip");
                chip.setMinWidth(0);
                chip.setMaxWidth(Double.MAX_VALUE);
                chip.setTextOverrun(OverrunStyle.ELLIPSIS);
                if (t.isDone()) chip.getStyleClass().add("cal-chip-done");
                else if (t.isOverdue()) chip.getStyleClass().add("cal-chip-late");
                cell.getChildren().add(chip);
                shown++;
            }
            if (ts.size() > 2) cell.getChildren().add(Ui.label("+" + (ts.size() - 2) + " lagi", "cal-more"));
        }
        return cell;
    }
}
