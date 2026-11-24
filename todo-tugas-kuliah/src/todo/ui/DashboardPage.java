package todo.ui;

/*
 * KUNCI JAWABAN. Cocokkan dengan nomor TODO di file kerangka:
 *   TODO 0A = build()
 *   TODO 1A-1D = buildCenter() (termasuk kolom cari dan sapaan)   TODO 1E = statCard()
 *   TODO 2A = buildListCard()   TODO 2B = refreshList()   TODO 2C = inActiveTab()   TODO 2D = compareImportance()
 *   TODO 4A = buildRight()   TODO 4B = drawMiniCalendar()   TODO 4C = buildReminders()   TODO 4D = reminderCard()
 */

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import todo.model.Task;

/**
 * Halaman Dashboard. Terdiri dari dua kolom:
 *   - tengah: pencarian, sapaan, 4 kartu angka, kartu "Daftar tugas"
 *   - kanan : "Agendamu" (mini kalender) + "Jangan sampai terlewat" (pengingat)
 */
final class DashboardPage {
    private final MainFrame app;
    private final TaskFilter filter = new TaskFilter();

    // ---- Keadaan halaman (diingat selama aplikasi berjalan) ----
    private int tab = 0;                         // tab aktif: 0 Semua, 1 Hari ini, 2 Mendatang, 3 Selesai
    private YearMonth month = YearMonth.now();   // bulan yang tampil di mini kalender
    private boolean showReminders = true;        // panel pengingat tampil atau tidak (tombol lonceng)

    // ---- Bagian layar yang diisi ulang tanpa membangun ulang seluruh halaman ----
    private VBox listBox;        // isi kartu "Daftar tugas"
    private VBox calHolder;      // tempat grid mini kalender
    private Label monthLabel;    // tulisan "November 2026"

    DashboardPage(MainFrame app) { this.app = app; }

    /** Menyusun halaman: kolom tengah (bisa di-scroll) + panel kanan (lebar tetap). */
    Node build() {
        ScrollPane center = new ScrollPane(buildCenter());
        center.setFitToWidth(true);
        center.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        center.getStyleClass().add("page-scroll");
        Ui.grow(center);                       // kolom tengah mengisi sisa lebar

        return new HBox(center, buildRight());
    }

    // =====================================================================
    // KOLOM TENGAH
    // =====================================================================
    private Node buildCenter() {
        // --- Hitung angka untuk sapaan dan kartu ---
        List<Task> all = app.getTasks();
        int total = all.size();
        int done = 0;
        int dueToday = 0;
        for (Task t : all) {
            if (t.isDone()) done++;
            if (!t.isDone() && t.getDeadline().toLocalDate().equals(LocalDate.now())) dueToday++;
        }

        // --- Baris atas: kolom cari + tanggal hari ini ---
        TextField search = new TextField(filter.keyword);
        search.setPromptText("Cari tugas atau mata kuliah...");
        search.getStyleClass().add("plain-field");
        // Setiap huruf yang diketik: simpan kata kuncinya, lalu isi ulang daftar
        search.textProperty().addListener((o, oldText, newText) -> {
            filter.keyword = newText;
            refreshList();
        });
        HBox searchBox = new HBox(8, Ui.icon(Ui.SEARCH, 18, "icon-muted"), Ui.grow(search));
        searchBox.getStyleClass().add("search-box");
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPrefWidth(350);
        searchBox.setMaxWidth(350);
        HBox top = new HBox(searchBox, Ui.spacer(), Ui.label(LocalDate.now().format(Ui.LONG_DATE), "date-label"));
        top.setAlignment(Pos.CENTER_LEFT);

        // --- Sapaan + tombol Tambah tugas ---
        String sub = (dueToday == 0)
                ? "Tidak ada tugas yang jatuh tempo hari ini."
                : "Ada " + dueToday + " tugas yang perlu perhatianmu hari ini.";
        VBox greeting = new VBox(4,
                Ui.label("SELAMAT DATANG KEMBALI", "welcome-caption"),
                Ui.label("Bikin hari ini lebih terarah.", "headline"),
                Ui.label(sub, "page-sub"));
        Button add = new Button("Tambah tugas", Ui.icon(Ui.PLUS, 16, "icon-white"));
        add.getStyleClass().add("btn-primary");
        add.setOnAction(e -> app.openAdd());
        HBox header = new HBox(greeting, Ui.spacer(), add);
        header.setAlignment(Pos.CENTER_LEFT);

        // --- Empat kartu angka ---
        HBox stats = new HBox(16,
                statCard(total, "Total tugas", null),
                statCard(total - done, "Belum selesai", null),
                statCard(done, "Selesai", "stat-card-done"),
                statCard(app.getDueSoon().size(), "Deadline dekat", "stat-card-soon"));

        VBox page = new VBox(24, top, header, stats, buildListCard());
        page.getStyleClass().add("center-area");
        return page;
    }

    /** Satu kartu angka. styleClass boleh null (kartu putih biasa). */
    private Node statCard(int number, String label, String styleClass) {
        Label num = Ui.label(String.valueOf(number), "stat-number");
        num.setMinWidth(Region.USE_PREF_SIZE);   // angka tidak boleh terpotong
        Label name = Ui.label(label, "stat-label");
        name.setWrapText(true);                  // label boleh turun baris kalau sempit
        name.setMinWidth(0);

        HBox card = new HBox(10, num, name);
        card.getStyleClass().add("stat-card");
        if (styleClass != null) card.getStyleClass().add(styleClass);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(Double.MAX_VALUE);
        Ui.grow(card);
        card.setPrefWidth(10);                   // prefWidth sama kecil + grow = keempat kartu sama lebar
        return card;
    }

    // ---------------------------------------------------------------------
    // Kartu "Daftar tugas": judul, tombol Filter, 4 tab, lalu daftar
    // ---------------------------------------------------------------------
    private Node buildListCard() {
        Button filterBtn = filter.button(app, this::refreshList);   // this::refreshList = "panggil ini setelah filter berubah"
        HBox head = new HBox(
                new VBox(3, Ui.label("Daftar tugas", "card-title"),
                            Ui.label("Dahulukan yang paling penting.", "card-sub")),
                Ui.spacer(), filterBtn);

        // Empat tab. Memilih tab = mengganti isi daftar saja.
        ToggleGroup group = new ToggleGroup();
        HBox tabs = new HBox(6);
        String[] names = {"Semua tugas", "Hari ini", "Mendatang", "Selesai"};
        for (int i = 0; i < names.length; i++) {
            final int number = i;
            ToggleButton b = new ToggleButton(names[i]);
            b.setToggleGroup(group);
            b.getStyleClass().add("tab-btn");
            b.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> { if (b.isSelected()) e.consume(); });
            b.setOnAction(e -> { tab = number; refreshList(); });
            b.setSelected(i == tab);
            tabs.getChildren().add(b);
        }

        listBox = new VBox();
        refreshList();

        VBox card = new VBox(16, head, tabs, listBox);
        card.getStyleClass().add("card");
        return card;
    }

    /**
     * Mengisi ulang isi daftar tugas. Hanya listBox yang diganti (bukan seluruh halaman),
     * supaya kolom pencarian tidak kehilangan fokus saat pengguna mengetik.
     */
    private void refreshList() {
        listBox.getChildren().clear();

        // 1. Kumpulkan tugas yang lolos pencarian/filter dan cocok dengan tab aktif
        List<Task> shown = new ArrayList<>();
        for (Task t : app.getTasks()) {
            if (filter.matches(t) && inActiveTab(t)) shown.add(t);
        }

        // 2. Urutkan: yang paling penting di atas
        shown.sort((a, b) -> compareImportance(a, b));

        // 3. Tampilkan
        if (shown.isEmpty()) listBox.getChildren().add(Ui.emptyState("Belum ada tugas"));
        for (Task t : shown) listBox.getChildren().add(Ui.taskRow(t, app));
    }

    /** Apakah tugas ini termasuk tab yang sedang aktif? */
    private boolean inActiveTab(Task t) {
        LocalDate deadlineDay = t.getDeadline().toLocalDate();
        LocalDate today = LocalDate.now();
        if (tab == 1) return !t.isDone() && deadlineDay.equals(today);       // Hari ini
        if (tab == 2) return !t.isDone() && deadlineDay.isAfter(today);      // Mendatang
        if (tab == 3) return t.isDone();                                     // Selesai
        return true;                                                         // Semua tugas
    }

    /** Urutan penting: belum selesai dulu, prioritas tinggi dulu, lalu deadline terdekat. */
    private int compareImportance(Task a, Task b) {
        if (a.isDone() != b.isDone()) return a.isDone() ? 1 : -1;
        int byPriority = b.getPriority().getLevel() - a.getPriority().getLevel();
        if (byPriority != 0) return byPriority;
        return a.getDeadline().compareTo(b.getDeadline());
    }

    // =====================================================================
    // PANEL KANAN: Agendamu + pengingat
    // =====================================================================
    private Node buildRight() {
        // Pengingat dibuat dulu karena tombol lonceng mengatur tampil/sembunyinya
        VBox reminderBox = buildReminders();
        reminderBox.setVisible(showReminders);
        reminderBox.setManaged(showReminders);    // managed=false: ruangnya ikut hilang, bukan cuma transparan

        Button bell = new Button();
        bell.setGraphic(Ui.icon(Ui.BELL, 18, "icon-dark"));
        bell.getStyleClass().add("circle-btn");
        bell.setOnAction(e -> {
            showReminders = !showReminders;
            reminderBox.setVisible(showReminders);
            reminderBox.setManaged(showReminders);
        });
        HBox title = new HBox(Ui.label("Agendamu", "panel-title"), Ui.spacer(), bell);
        title.setAlignment(Pos.CENTER_LEFT);

        // Mini kalender dengan tombol bulan sebelum/sesudah
        monthLabel = Ui.label("", "month-title");
        calHolder = new VBox();
        Button prev = new Button();
        prev.setGraphic(Ui.icon(Ui.LEFT, 16, "icon-dark"));
        prev.getStyleClass().add("circle-btn");
        prev.setOnAction(e -> { month = month.minusMonths(1); drawMiniCalendar(); });
        Button next = new Button();
        next.setGraphic(Ui.icon(Ui.RIGHT, 16, "icon-dark"));
        next.getStyleClass().add("circle-btn");
        next.setOnAction(e -> { month = month.plusMonths(1); drawMiniCalendar(); });
        HBox monthRow = new HBox(8, monthLabel, Ui.spacer(), prev, next);
        monthRow.setAlignment(Pos.CENTER_LEFT);
        drawMiniCalendar();

        VBox right = new VBox(18, title, monthRow, calHolder, Ui.label("", "divider"), reminderBox);
        right.getStyleClass().add("right-panel");
        right.setPrefWidth(368);
        right.setMinWidth(368);
        right.setMaxWidth(368);
        return right;
    }

    /** Menggambar mini kalender untuk bulan yang sedang dipilih. Klik tanggal = buka halaman Kalender. */
    private void drawMiniCalendar() {
        monthLabel.setText(month.getMonth().getDisplayName(TextStyle.FULL, Ui.ID) + " " + month.getYear());
        calHolder.getChildren().setAll(
                MonthGrid.build(month, LocalDate.now(), MonthGrid.byDate(app.getTasks()), false,
                        date -> app.showCalendar(date)));
    }

    // ---------------------------------------------------------------------
    // "Jangan sampai terlewat": 5 tugas belum selesai dengan deadline terdekat
    // ---------------------------------------------------------------------
    private VBox buildReminders() {
        int urgent = app.getOverdue().size() + app.getDueSoon().size();
        HBox head = new HBox(Ui.label("Jangan sampai terlewat", "panel-title-small"),
                Ui.spacer(), Ui.label(String.valueOf(urgent), "count-badge"));
        head.setAlignment(Pos.CENTER_LEFT);

        // Ambil tugas yang belum selesai, urutkan menurut deadline, ambil 5 pertama
        List<Task> upcoming = new ArrayList<>();
        for (Task t : app.getTasks()) {
            if (!t.isDone()) upcoming.add(t);
        }
        upcoming.sort((a, b) -> a.getDeadline().compareTo(b.getDeadline()));
        if (upcoming.size() > 5) upcoming = upcoming.subList(0, 5);

        VBox box = new VBox(12, head);
        if (upcoming.isEmpty()) box.getChildren().add(Ui.emptyState("Belum ada tugas"));
        for (Task t : upcoming) box.getChildren().add(reminderCard(t));
        return box;
    }

    private Node reminderCard(Task t) {
        // Warna garis kiri: merah = terlambat, kuning = kurang dari 24 jam, hijau = masih lama
        String color;
        if (t.isOverdue()) color = "rem-late";
        else if (t.isDueWithinHours(24)) color = "rem-soon";
        else color = "rem-ok";

        HBox bottom = new HBox(Ui.label(t.getCourse(), "rem-meta"), Ui.spacer(),
                Ui.label(Ui.relativeText(t), "rem-meta"));
        VBox card = new VBox(4, Ui.label(Ui.deadlineText(t), "rem-meta"), Ui.label(t.getTitle(), "rem-title"), bottom);
        card.getStyleClass().addAll("rem-card", color);
        card.setOnMouseClicked(e -> app.openEdit(t));
        return card;
    }
}
