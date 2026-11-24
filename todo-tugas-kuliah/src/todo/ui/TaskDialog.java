package todo.ui;

/*
 * KUNCI JAWABAN. Cocokkan dengan nomor TODO di file kerangka:
 *   TODO 1A-1G = seluruh isi konstruktor (judul, kolom isian, isi awal, susunan, tombol, validasi, hasil)
 */

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import todo.model.Task;

/**
 * Form tambah/ubah tugas (jendela pop-up).
 *   existing == null -> mode tambah (form kosong)
 *   existing != null -> mode ubah   (form terisi data tugas itu + kotak "sudah selesai")
 * Hasilnya diambil lewat getResult(): berisi Task kalau disimpan, null kalau dibatalkan.
 * (getResult() warisan dari class Dialog bawaan JavaFX.)
 */
public class TaskDialog extends Dialog<Task> {

    public TaskDialog(Window owner, Task existing, List<String> courses) {
        initOwner(owner);
        setTitle(existing == null ? "Tambah Tugas" : "Ubah Tugas");
        setHeaderText(null);

        // ---- Kolom isian ----
        TextField title = new TextField();
        title.setPromptText("Contoh: Laporan Praktikum Bab 3");

        ComboBox<String> course = new ComboBox<>(FXCollections.observableArrayList(courses));
        course.setEditable(true);                       // boleh pilih yang ada atau ketik baru
        course.setPromptText("Pilih atau ketik mata kuliah");
        course.setMaxWidth(Double.MAX_VALUE);

        DatePicker date = new DatePicker(LocalDate.now().plusDays(3));
        date.setMaxWidth(Double.MAX_VALUE);
        TextField time = new TextField("23:59");
        time.setPrefWidth(110);

        // Prioritas: tiga tombol segmen
        ToggleGroup prio = new ToggleGroup();
        HBox prioBox = new HBox(8);
        for (todo.model.Priority p : todo.model.Priority.values()) {
            ToggleButton b = new ToggleButton(p.getLabel());
            b.setUserData(p);
            b.setToggleGroup(prio);
            b.getStyleClass().add("seg-btn");
            b.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> { if (b.isSelected()) e.consume(); });
            if (p == todo.model.Priority.MEDIUM) b.setSelected(true);
            prioBox.getChildren().add(b);
        }

        TextArea notes = new TextArea();
        notes.setPromptText("Materi, link, atau hal yang perlu diingat");
        notes.setPrefRowCount(3);

        CheckBox done = new CheckBox("Tandai sudah selesai");

        // ---- Isi awal bila mode ubah ----
        if (existing != null) {
            title.setText(existing.getTitle());
            course.getEditor().setText(existing.getCourse());
            date.setValue(existing.getDeadline().toLocalDate());
            time.setText(existing.getDeadline().toLocalTime().withSecond(0).withNano(0).toString());
            for (javafx.scene.control.Toggle t : prio.getToggles()) {
                if (t.getUserData() == existing.getPriority()) t.setSelected(true);
            }
            notes.setText(existing.getNotes());
            done.setSelected(existing.isDone());
        }

        // ---- Susunan form ----
        VBox form = new VBox(14,
                Ui.label(existing == null ? "Tambah tugas" : "Ubah tugas", "dialog-title"),
                field("Judul tugas *", title, "Nama tugas yang akan dikerjakan"),
                field("Mata kuliah *", course, "Pilih dari daftar, atau ketik nama mata kuliah baru"),
                field("Deadline *", new HBox(10, Ui.grow(date), time), "Tanggal dan jam (24 jam, format HH:mm, contoh 23:59)"),
                field("Prioritas", prioBox, "Tinggi untuk tugas mendesak, Rendah untuk yang santai"),
                field("Catatan", notes, "Opsional"));
        if (existing != null) form.getChildren().add(done);
        form.setPadding(new Insets(6));
        form.setPrefWidth(480);

        ButtonType save = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        getDialogPane().setContent(form);
        getDialogPane().getStyleClass().add("task-dialog");
        if (Ui.cssUrl != null) getDialogPane().getStylesheets().add(Ui.cssUrl);

        Button saveBtn = (Button) getDialogPane().lookupButton(save);
        saveBtn.getStyleClass().add("btn-primary");
        ((Button) getDialogPane().lookupButton(ButtonType.CANCEL)).getStyleClass().add("btn-ghost");

        // Validasi sebelum dialog ditutup (isian tidak hilang bila salah)
        saveBtn.addEventFilter(javafx.event.ActionEvent.ACTION, ev -> {
            String err = validate(title, course, date, time, existing);
            if (err != null) {
                Alert a = new Alert(Alert.AlertType.WARNING, err);
                a.setHeaderText(null);
                a.initOwner(getDialogPane().getScene().getWindow());
                a.showAndWait();
                ev.consume();
            }
        });

        setResultConverter(bt -> {
            if (bt != save) return null;      // dibatalkan
            LocalDateTime dl = LocalDateTime.of(date.getValue(), LocalTime.parse(time.getText().trim()));
            todo.model.Priority p = (todo.model.Priority) prio.getSelectedToggle().getUserData();
            String c = course.getEditor().getText().trim();
            if (existing != null) {
                // Task dengan id lama; MainFrame yang menerapkan perubahannya lewat TaskManager
                return new Task(existing.getId(), title.getText().trim(), c, dl, p, notes.getText(), done.isSelected());
            }
            return new Task(title.getText().trim(), c, dl, p, notes.getText());
        });
    }

    private static Node field(String label, Node input, String hint) {
        VBox box = new VBox(4, Ui.label(label, "field-label"), input, Ui.label(hint, "field-hint"));
        return box;
    }

    /** Mengembalikan pesan kesalahan, atau null bila valid. */
    private static String validate(TextField title, ComboBox<String> course, DatePicker date, TextField time, Task existing) {
        if (title.getText().isBlank()) return "Judul wajib diisi.";
        if (course.getEditor().getText().isBlank()) return "Mata kuliah wajib diisi.";
        if (date.getValue() == null) return "Tanggal deadline wajib diisi.";
        LocalTime lt;
        try {
            lt = LocalTime.parse(time.getText().trim());
        } catch (DateTimeParseException e) {
            return "Format jam harus HH:mm, contoh 23:59.";
        }
        LocalDateTime dl = LocalDateTime.of(date.getValue(), lt);
        boolean changed = existing == null || !dl.equals(existing.getDeadline().withSecond(0).withNano(0));
        // TaskManager menolak deadline yang sudah lewat; dicek di sini agar isian tidak hilang
        if (changed && dl.isBefore(LocalDateTime.now())) return "Deadline tidak boleh sudah lewat.";
        return null;
    }
}
