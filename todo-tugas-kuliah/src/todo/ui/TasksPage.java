package todo.ui;

/*
 * KUNCI JAWABAN. Cocokkan dengan nomor TODO di file kerangka:
 *   TODO 0A = build()
 *   TODO 1A = buildInfo()   TODO 1B = semester()
 *   TODO 2A, 2B = buildToolbar() (termasuk tombol Urutkan)
 *   TODO 3A = renderContent()   TODO 3B = compareBySort()   TODO 3C = listView()
 *   TODO 4A = board()   TODO 4B = column()   TODO 4C = card()
 */

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import todo.model.Task;

/**
 * Halaman "Semua tugas". Susunan dari atas ke bawah:
 *   info (periode, target, mata kuliah) + kolom cari  ->  toolbar (tombol tambah, tab tampilan, filter, urutkan)
 *   ->  isi: salah satu dari Papan (kanban) / Daftar / Kalender
 */
final class TasksPage {
    private final MainFrame app;
    private final TaskFilter filter = new TaskFilter();
    private final CalendarPage calendar;

    // ---- Keadaan halaman ----
    private int view = 0;                  // tampilan aktif: 0 Papan, 1 Daftar, 2 Kalender
    private String sort = "Deadline";      // urutan: Deadline / Prioritas / Judul
    private final VBox content = new VBox();   // tempat isi (papan/daftar/kalender); diganti saat tab berubah

    TasksPage(MainFrame app) {
        this.app = app;
        this.calendar = new CalendarPage(app);
    }

    Node build() {
        List<String> courses = app.getCourses();

        VBox page = new VBox(18, buildInfo(courses), buildToolbar(), content);
        page.getStyleClass().add("center-area");
        renderContent();

        ScrollPane sp = new ScrollPane(page);
        sp.setFitToWidth(true);
        sp.getStyleClass().add("page-scroll");
        return sp;
    }

    // =====================================================================
    // 1. Bagian atas: periode, target, chip mata kuliah, kolom cari
    // =====================================================================
    private Node buildInfo(List<String> courses) {
        GridPane info = new GridPane();       // GridPane = tabel: add(isi, kolom, baris)
        info.setHgap(30);
        info.setVgap(12);
        info.add(Ui.label("Periode", "info-key"), 0, 0);
        info.add(Ui.label(semester(), "info-val"), 1, 0);
        info.add(Ui.label("Target", "info-key"), 0, 1);
        info.add(Ui.label("Selesaikan tugas sebelum deadline", "info-val"), 1, 1);
        info.add(Ui.label("Mata kuliah", "info-key"), 0, 2);

        // Satu "chip" berwarna per mata kuliah
        HBox chips = new HBox(8);
        chips.setAlignment(Pos.CENTER_LEFT);
        if (courses.isEmpty()) chips.getChildren().add(Ui.label("Belum ada", "info-val"));
        for (String c : courses) {
            chips.getChildren().add(Ui.label(c, "chip", "chip-c" + Ui.colorIndex(c, courses)));
        }
        info.add(chips, 1, 2);

        // Kolom cari: setiap huruf diketik, papan digambar ulang
        TextField search = new TextField(filter.keyword);
        search.setPromptText("Cari tugas di papan...");
        search.getStyleClass().add("plain-field");
        search.textProperty().addListener((o, oldText, newText) -> {
            filter.keyword = newText;
            renderContent();
        });
        HBox searchBox = new HBox(8, Ui.icon(Ui.SEARCH, 18, "icon-muted"), Ui.grow(search));
        searchBox.getStyleClass().add("search-box");
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPrefWidth(280);
        searchBox.setMaxWidth(280);

        Label count = Ui.label(app.getTasks().size() + " tugas  ·  " + courses.size() + " mata kuliah", "row-meta");
        VBox right = new VBox(14, searchBox, count);
        right.setAlignment(Pos.TOP_RIGHT);

        return new HBox(info, Ui.spacer(), right);
    }

    /** Semester dihitung dari tanggal hari ini: Agustus-Januari = ganjil, Februari-Juli = genap. */
    private static String semester() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        boolean ganjil = (month >= 8 || month == 1);
        int startYear = (month >= 8) ? today.getYear() : today.getYear() - 1;
        return "Semester " + (ganjil ? "ganjil" : "genap") + " " + startYear + "/" + (startYear + 1);
    }

    // =====================================================================
    // 2. Toolbar: Tambah tugas | Papan Daftar Kalender | Filter Urutkan
    // =====================================================================
    private Node buildToolbar() {
        Button add = new Button("Tambah tugas", Ui.icon(Ui.PLUS, 16, "icon-white"));
        add.getStyleClass().add("btn-primary");
        add.setOnAction(e -> app.openAdd());

        // Tiga tab tampilan
        ToggleGroup group = new ToggleGroup();
        HBox tabs = new HBox(8);
        String[] names = {"Papan", "Daftar", "Kalender"};
        String[] icons = {Ui.BOARD, Ui.LIST, Ui.CALENDAR};
        for (int i = 0; i < names.length; i++) {
            final int number = i;
            ToggleButton b = new ToggleButton(names[i], Ui.icon(icons[i], 17, "icon-dark"));
            b.setToggleGroup(group);
            b.getStyleClass().add("view-tab");
            b.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> { if (b.isSelected()) e.consume(); });
            b.setOnAction(e -> { view = number; renderContent(); });
            b.setSelected(i == view);
            tabs.getChildren().add(b);
        }

        Button filterBtn = filter.button(app, this::renderContent);

        // Tombol Urutkan membuka menu kecil berisi tiga pilihan
        Button sortBtn = new Button("Urutkan", Ui.icon(Ui.SORT, 15, "icon-dark"));
        sortBtn.getStyleClass().add("pill-btn");
        sortBtn.setOnAction(e -> {
            ContextMenu menu = new ContextMenu();
            for (String option : new String[]{"Deadline", "Prioritas", "Judul"}) {
                MenuItem item = new MenuItem((option.equals(sort) ? "✓  " : "") + option);
                item.setOnAction(ev -> { sort = option; renderContent(); });
                menu.getItems().add(item);
            }
            menu.show(sortBtn, Side.BOTTOM, 0, 4);
        });

        HBox bar = new HBox(20, add, tabs, Ui.spacer(), filterBtn, sortBtn);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    // =====================================================================
    // 3. Isi halaman: papan / daftar / kalender
    // =====================================================================

    /** Mengganti isi sesuai tampilan aktif. Dipanggil saat tab, filter, urutan, atau pencarian berubah. */
    private void renderContent() {
        content.getChildren().clear();

        if (view == 2) {                                   // Kalender
            content.getChildren().add(calendar.build(false));
            return;
        }

        // Ambil tugas yang lolos pencarian + filter, lalu urutkan
        List<Task> list = new ArrayList<>();
        for (Task t : app.getTasks()) {
            if (filter.matches(t)) list.add(t);
        }
        list.sort((a, b) -> compareBySort(a, b));

        content.getChildren().add(view == 0 ? board(list) : listView(list));
    }

    /** Membandingkan dua tugas menurut pilihan "Urutkan". */
    private int compareBySort(Task a, Task b) {
        if (sort.equals("Prioritas")) {
            int byPriority = b.getPriority().getLevel() - a.getPriority().getLevel();   // tinggi dulu
            if (byPriority != 0) return byPriority;
        }
        if (sort.equals("Judul")) {
            return a.getTitle().compareToIgnoreCase(b.getTitle());
        }
        return a.getDeadline().compareTo(b.getDeadline());                              // deadline terdekat dulu
    }

    // ---- Tampilan Daftar ----
    private Node listView(List<Task> list) {
        VBox card = new VBox();
        card.getStyleClass().add("card");
        if (list.isEmpty()) card.getChildren().add(Ui.emptyState("Belum ada tugas"));
        for (Task t : list) card.getChildren().add(Ui.taskRow(t, app));
        return card;
    }

    // ---- Tampilan Papan (kanban): 4 kolom ----
    private Node board(List<Task> list) {
        // Bagi tugas ke empat kolom menurut statusnya (lihat Ui.stageOf)
        List<Task> todo = new ArrayList<>();
        List<Task> doing = new ArrayList<>();
        List<Task> done = new ArrayList<>();
        List<Task> late = new ArrayList<>();
        for (Task t : list) {
            Ui.Phase phase = Ui.stageOf(t);
            if (phase == Ui.Phase.TODO) todo.add(t);
            else if (phase == Ui.Phase.DOING) doing.add(t);
            else if (phase == Ui.Phase.DONE) done.add(t);
            else late.add(t);
        }

        List<String> courses = app.getCourses();
        HBox columns = new HBox(18,
                column("Belum mulai", "#C0B6AC", todo, courses),
                column("Dikerjakan", "#FFD21F", doing, courses),
                column("Selesai", "#76944C", done, courses),
                column("Terlambat", "#8A6A55", late, courses));
        columns.setAlignment(Pos.TOP_LEFT);
        return columns;
    }

    /** Satu kolom papan: judul + jumlah + tombol plus, kartu-kartu, lalu "Tambah tugas". */
    private Node column(String name, String dotColor, List<Task> items, List<String> courses) {
        Button plus = new Button();
        plus.setGraphic(Ui.icon(Ui.PLUS, 18, "icon-dark"));
        plus.getStyleClass().add("icon-btn");
        plus.setOnAction(e -> app.openAdd());

        HBox head = new HBox(8,
                new Circle(4, Color.web(dotColor)),
                Ui.label(name, "col-title"),
                Ui.label(String.valueOf(items.size()), "count-badge"),
                Ui.spacer(), plus);
        head.setAlignment(Pos.CENTER_LEFT);

        VBox cards = new VBox(12);
        for (Task t : items) cards.getChildren().add(card(t, courses));

        Button addText = new Button("Tambah tugas", Ui.icon(Ui.PLUS, 14, "icon-muted"));
        addText.getStyleClass().add("ghost-btn");
        addText.setOnAction(e -> app.openAdd());

        VBox column = new VBox(14, head, cards, addText);
        column.setMinWidth(220);
        column.setPrefWidth(10);       // prefWidth kecil + grow = keempat kolom sama lebar
        Ui.grow(column);
        return column;
    }

    /** Satu kartu tugas di papan. */
    private Node card(Task t, List<String> courses) {
        Ui.Phase phase = Ui.stageOf(t);

        // Baris atas: ikon + waktu (kiri), tombol panah untuk mengubah (kanan)
        String when = (phase == Ui.Phase.DONE)
                ? "Selesai · " + t.getDeadline().toLocalDate().format(Ui.DAY)
                : Ui.deadlineText(t);
        String iconPath = (phase == Ui.Phase.DONE) ? Ui.CHECK : Ui.CLOCK;
        HBox time = new HBox(6, Ui.icon(iconPath, 14, "icon-dark"), Ui.label(when, "kcard-meta"));
        time.setAlignment(Pos.CENTER_LEFT);
        Button arrow = new Button();
        arrow.setGraphic(Ui.icon(Ui.ARROW, 14, "icon-dark"));
        arrow.getStyleClass().add("arrow-btn");
        arrow.setOnAction(e -> app.openEdit(t));
        HBox top = new HBox(time, Ui.spacer(), arrow);
        top.setAlignment(Pos.CENTER_LEFT);

        // Judul
        Label title = Ui.label(t.getTitle(), "kcard-title");
        title.setWrapText(true);
        if (phase == Ui.Phase.DONE) title.getStyleClass().add("kcard-title-done");

        // Baris bawah: mata kuliah + badge (selesai / lewat berapa hari / prioritas)
        Label badge;
        if (phase == Ui.Phase.DONE) badge = Ui.label("Selesai", "badge", "badge-done");
        else if (phase == Ui.Phase.LATE) badge = Ui.label(Ui.relativeText(t), "badge", "badge-late");
        else badge = Ui.priorityBadge(t.getPriority());
        VBox bottom = new VBox(6, Ui.label(t.getCourse(), "kcard-meta"), new HBox(badge));

        // Ruang kosong elastis supaya bagian bawah selalu menempel di dasar kartu
        StackPane gap = new StackPane();
        gap.setMinHeight(14);
        Ui.vgrow(gap);

        VBox card = new VBox(8, top, title, gap, bottom);
        card.getStyleClass().add("kcard");

        // Warna kartu: kelabu-hijau (selesai), cokelat (terlambat), atau menurut mata kuliah
        if (phase == Ui.Phase.DONE) card.getStyleClass().add("kcard-done");
        else if (phase == Ui.Phase.LATE) card.getStyleClass().add("kcard-late");
        else card.getStyleClass().add("kcard-c" + Ui.colorIndex(t.getCourse(), courses));

        // Klik kanan: menu Ubah / Tandai selesai / Hapus
        card.setOnContextMenuRequested(e -> Ui.taskMenu(t, app).show(card, e.getScreenX(), e.getScreenY()));
        return card;
    }
}
