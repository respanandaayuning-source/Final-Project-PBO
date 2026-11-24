package todo.ui;

/*
 * KUNCI JAWABAN. Cocokkan dengan nomor TODO di file kerangka:
 *   TODO 0A, 0B  = field dan konstruktor
 *   TODO 1A-1D   = show()
 *   TODO 2A-2F   = buildSidebar() dan refreshCourses()
 *   TODO 3A-3G   = toggleSidebar() dan applySidebarLook()  (sidebar buka/tutup)
 *   TODO 4A-4D   = getTasks(), getOverdue(), getDueSoon(), getCourses()
 *   TODO 5A-5F   = openAdd(), openEdit(), applyEdit(), delete(), toggle(), run()
 *   TODO 6A-6C   = render(), navigate(), showCalendar()
 *   TODO 7A      = findCss()
 */

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import todo.model.Task;
import todo.service.ReminderService;
import todo.service.TaskManager;

/*
 * ======================================================================
 * PETA KODE (baca urut dari sini)
 *
 *  App.java      membuat TaskManager + ReminderService (backend), lalu memanggil MainFrame.show().
 *  MainFrame     kerangka jendela: sidebar kiri + area halaman di kanan.
 *                Hanya class ini yang memanggil TaskManager (tambah, ubah, hapus, centang),
 *                jadi halaman lain cukup meminta lewat app.openAdd(), app.toggle(task), dst.
 *  Halaman       DashboardPage, TasksPage, CalendarPage, StatsPage.
 *                Masing-masing punya build() yang mengembalikan tampilan dari data terbaru.
 *  Pendukung     Ui (ikon, label, baris tugas), MonthGrid (grid kalender),
 *                TaskFilter (pencarian + filter), TaskDialog (form tambah/ubah),
 *                style.css (SEMUA warna, ukuran, dan bentuk).
 *
 *  ALUR SATU AKSI, contoh mencentang tugas:
 *    klik checkbox -> app.toggle(task) -> run(...) -> manager.toggleTaskDone(id) -> render()
 *    render() membangun ulang halaman aktif dari data terbaru, jadi tampilan selalu sinkron
 *    dengan data. Itu sebabnya halaman tidak perlu "mengingat" perubahan sendiri.
 *
 *  CARA MEMBACA KODE JAVAFX
 *    - Layout:  VBox = susun ke bawah, HBox = susun ke samping, BorderPane = kiri/tengah/kanan.
 *    - Gaya:    node.getStyleClass().add("nama") -> aturan ".nama" di style.css yang mengatur tampilannya.
 *    - Aksi:    button.setOnAction(e -> ...) = "kalau diklik, jalankan kode ini".
 * ======================================================================
 */

/** Jendela utama aplikasi JavaFX: sidebar + halaman. (Anggota 2) */
public class MainFrame {

    private final TaskManager manager;
    private final ReminderService reminder;

    private Stage stage;
    private final StackPane pageHost = new StackPane();               // tempat halaman aktif ditampilkan
    private final List<ToggleButton> navButtons = new ArrayList<>();  // 4 tombol menu sidebar
    private final VBox courseList = new VBox(14);                     // daftar mata kuliah di sidebar
    private int current = 0;          // halaman aktif: 0 Dashboard, 1 Semua tugas, 2 Kalender, 3 Statistik

    // ---- Sidebar buka/tutup ----
    private static final double EXPANDED_WIDTH = 224;    // sidebar terbuka
    private static final double COLLAPSED_WIDTH = 83;    // sidebar ciut (ikon saja)
    private VBox sidebar;                                // disimpan di field supaya toggleSidebar() bisa mengubahnya
    private boolean collapsed = false;
    private HBox brandRow;                               // baris logo (rata kiri saat terbuka, tengah saat ciut)
    private HBox toggleRow;                              // baris tombol hamburger
    private final List<Node> sidebarTexts = new ArrayList<>();   // semua teks yang disembunyikan saat ciut

    private final DashboardPage dashboard;
    private final TasksPage tasksPage;
    private final CalendarPage calendarPage;
    private final StatsPage statsPage;

    public MainFrame(TaskManager manager, ReminderService reminder) {
        this.manager = manager;
        this.reminder = reminder;
        // Tiap halaman diberi "this" supaya bisa meminta aksi (tambah, hapus, dst) ke MainFrame
        this.dashboard = new DashboardPage(this);
        this.tasksPage = new TasksPage(this);
        this.calendarPage = new CalendarPage(this);
        this.statsPage = new StatsPage(this);
    }

    // =====================================================================
    // 1. Jendela
    // =====================================================================
    public void show(Stage stage) {
        this.stage = stage;

        // Kerangka: sidebar di kiri, halaman di tengah
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setLeft(buildSidebar());
        root.setCenter(pageHost);

        Scene scene = new Scene(root, 1280, 800);

        // Pasang style.css (seluruh tampilan diatur di file itu)
        String css = findCss();
        Ui.cssUrl = css;                       // disimpan agar TaskDialog memakai CSS yang sama
        if (css != null) scene.getStylesheets().add(css);
        else System.err.println("Gagal memuat file CSS: style.css");

        // Ciut otomatis saat jendela menyempit di bawah 1200, dan terbuka lagi saat melebar
        scene.widthProperty().addListener((o, oldW, newW) -> {
            boolean wasWide = oldW.doubleValue() >= 1200;
            boolean isWide = newW.doubleValue() >= 1200;
            if (wasWide && !isWide && !collapsed) toggleSidebar();
            if (!wasWide && isWide && collapsed) toggleSidebar();
        });

        render();

        stage.setTitle("studyplan - Manajemen Tugas Kuliah");
        stage.setScene(scene);
        stage.setMinWidth(1100);
        stage.setMinHeight(600);
        stage.show();
    }

    /** Menggambar ulang halaman aktif + daftar mata kuliah. Dipanggil setiap data atau halaman berubah. */
    private void render() {
        Node page;
        if (current == 0) page = dashboard.build();
        else if (current == 1) page = tasksPage.build();
        else if (current == 2) page = calendarPage.build(true);
        else page = statsPage.build();

        pageHost.getChildren().setAll(page);
        refreshCourses();

        // Tandai tombol menu yang sesuai dengan halaman aktif
        for (int i = 0; i < navButtons.size(); i++) {
            navButtons.get(i).setSelected(i == current);
        }
    }

    // =====================================================================
    // 2. Sidebar (logo, 4 menu, daftar mata kuliah)
    // =====================================================================
    private VBox buildSidebar() {
        // Tombol hamburger di baris paling atas: klik = buka/tutup sidebar
        Button toggleBtn = new Button();
        toggleBtn.setGraphic(Ui.icon(Ui.MENU, 20, "icon-menu"));
        toggleBtn.getStyleClass().add("toggle-sidebar-btn");
        toggleBtn.setOnAction(e -> toggleSidebar());
        toggleRow = new HBox(toggleBtn);
        toggleRow.setAlignment(Pos.CENTER_RIGHT);

        // Logo + nama aplikasi
        StackPane logoBox = new StackPane(Ui.icon(Ui.BOOK, 22, "icon-olive"));
        logoBox.getStyleClass().add("logo-box");
        Label logoText = Ui.label("studyplan", "logo-text");
        brandRow = new HBox(10, logoBox, logoText);
        brandRow.setAlignment(Pos.CENTER_LEFT);
        Label caption = Ui.label("RUANG BELAJARMU", "sidebar-caption");
        VBox brand = new VBox(8, brandRow, caption);

        // Empat tombol menu. ToggleGroup memastikan hanya satu yang aktif.
        String[] names = {"Dashboard", "Semua tugas", "Kalender", "Statistik"};
        String[] icons = {Ui.GRID, Ui.LIST, Ui.CALENDAR, Ui.CHART};
        ToggleGroup group = new ToggleGroup();
        VBox menu = new VBox(8);
        for (int i = 0; i < names.length; i++) {
            final int page = i;   // "final" karena dipakai di dalam lambda di bawah
            ToggleButton b = new ToggleButton(names[i], Ui.icon(icons[i], 22, "icon-menu"));
            b.setToggleGroup(group);
            b.getStyleClass().add("menu-item");
            b.setGraphicTextGap(16);
            b.setMaxWidth(Double.MAX_VALUE);
            // Menu yang sedang aktif tidak boleh "terlepas" kalau diklik lagi
            b.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> { if (b.isSelected()) e.consume(); });
            b.setOnAction(e -> navigate(page));
            navButtons.add(b);
            menu.getChildren().add(b);
        }

        VBox courses = new VBox(14, Ui.label("MATA KULIAH", "sidebar-caption"), courseList);

        // Teks-teks yang hilang saat sidebar ciut (menu tidak masuk daftar ini: ia hanya
        // berganti ke mode "ikon saja" lewat setContentDisplay)
        sidebarTexts.add(logoText);
        sidebarTexts.add(caption);
        sidebarTexts.add(courses);

        sidebar = new VBox(toggleRow, brand, menu, courses);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(EXPANDED_WIDTH);
        // min dan max "mengikuti" pref (binding), jadi cukup mengubah prefWidth saja nanti
        sidebar.minWidthProperty().bind(sidebar.prefWidthProperty());
        sidebar.maxWidthProperty().bind(sidebar.prefWidthProperty());
        return sidebar;
    }

    // =====================================================================
    // 2B. Sidebar buka/tutup
    // =====================================================================

    /** Membuka atau menutup sidebar dengan animasi 200 milidetik. */
    private void toggleSidebar() {
        collapsed = !collapsed;
        double target = collapsed ? COLLAPSED_WIDTH : EXPANDED_WIDTH;

        // Ciut: sembunyikan teks DULU, baru mengecil (supaya teks tidak terpotong di tengah animasi)
        if (collapsed) applySidebarLook();

        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(200), new KeyValue(sidebar.prefWidthProperty(), target)));
        // Terbuka: melebar DULU, baru teks muncul
        if (!collapsed) animation.setOnFinished(e -> applySidebarLook());
        animation.play();
    }

    /** Menyesuaikan isi sidebar dengan keadaan "collapsed": teks tampil/hilang dan posisi rata kiri/tengah. */
    private void applySidebarLook() {
        for (Node n : sidebarTexts) {
            n.setVisible(!collapsed);
            n.setManaged(!collapsed);      // managed=false: ruangnya ikut hilang, bukan cuma transparan
        }
        for (ToggleButton b : navButtons) {
            b.setContentDisplay(collapsed ? ContentDisplay.GRAPHIC_ONLY : ContentDisplay.LEFT);
        }
        brandRow.setAlignment(collapsed ? Pos.CENTER : Pos.CENTER_LEFT);
        toggleRow.setAlignment(collapsed ? Pos.CENTER : Pos.CENTER_RIGHT);

        // Class CSS tambahan untuk padding dan perataan saat ciut
        sidebar.getStyleClass().remove("sidebar-collapsed");
        if (collapsed) sidebar.getStyleClass().add("sidebar-collapsed");
    }

    /** Mengisi daftar mata kuliah di sidebar dari data tugas (warna titik bergantian). */
    private void refreshCourses() {
        courseList.getChildren().clear();
        List<String> courses = getCourses();
        for (String c : courses) {
            Circle dot = new Circle(4, Color.web(Ui.DOT_COLORS[Ui.colorIndex(c, courses)]));
            HBox row = new HBox(12, dot, Ui.label(c, "course-name"));
            row.setAlignment(Pos.CENTER_LEFT);
            courseList.getChildren().add(row);
        }
        if (courses.isEmpty()) courseList.getChildren().add(Ui.label("Belum ada", "course-name"));
    }

    // =====================================================================
    // 3. Data untuk halaman (hanya MEMBACA dari backend)
    // =====================================================================
    public List<Task> getTasks() { return manager.getAll(); }

    public List<Task> getOverdue() { return reminder.getOverdue(); }   // dari ReminderService
    public List<Task> getDueSoon() { return reminder.getDueSoon(); }   // deadline dalam 24 jam

    /** Nama mata kuliah unik, urut abjad. */
    public List<String> getCourses() {
        List<String> result = new ArrayList<>();
        for (Task t : manager.getAll()) {
            String c = t.getCourse();
            if (c != null && !c.isBlank() && !result.contains(c)) result.add(c);
        }
        Collections.sort(result);
        return result;
    }

    // =====================================================================
    // 4. Aksi (MENGUBAH data lewat backend), dipanggil dari halaman
    // =====================================================================

    /** Membuka form tambah tugas. Kalau disimpan, tugas ditambahkan ke TaskManager. */
    public void openAdd() {
        TaskDialog dialog = new TaskDialog(stage, null, getCourses());
        dialog.showAndWait();                       // program berhenti di sini sampai dialog ditutup
        Task result = dialog.getResult();           // null kalau pengguna menekan Batal
        if (result != null) run(() -> manager.addTask(result));
    }

    /** Membuka form ubah tugas. */
    public void openEdit(Task t) {
        TaskDialog dialog = new TaskDialog(stage, t, getCourses());
        dialog.showAndWait();
        Task result = dialog.getResult();
        if (result != null) run(() -> applyEdit(result));
    }

    /** result = Task hasil form (id sama dengan tugas lama). */
    private void applyEdit(Task result) {
        Task old = manager.findById(result.getId());
        boolean deadlineSame = old != null && result.getDeadline().equals(old.getDeadline());
        if (deadlineSame) {
            // setDeadline() menolak tanggal yang sudah lewat, jadi tugas terlambat
            // diubah lewat setter lain saja (deadline-nya memang tidak berubah).
            old.setTitle(result.getTitle());
            old.setCourse(result.getCourse());
            old.setPriority(result.getPriority());
            old.setNotes(result.getNotes());
            old.setDone(result.isDone());
        } else {
            manager.updateTask(result.getId(), result.getTitle(), result.getCourse(),
                    result.getDeadline(), result.getPriority(), result.getNotes(), result.isDone());
        }
    }

    /** Menghapus tugas setelah konfirmasi. */
    public void delete(Task t) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Hapus tugas \"" + t.getTitle() + "\"?",
                ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        a.initOwner(stage);
        a.showAndWait();
        if (a.getResult() == ButtonType.YES) run(() -> manager.deleteTask(t.getId()));
    }

    /** Menandai selesai / belum selesai. */
    public void toggle(Task t) {
        run(() -> manager.toggleTaskDone(t.getId()));
    }

    /** Pindah halaman: 0 Dashboard, 1 Semua tugas, 2 Kalender, 3 Statistik. */
    public void navigate(int page) {
        current = page;
        render();
    }

    /** Dari mini kalender di Dashboard: buka halaman Kalender di tanggal tertentu. */
    public void showCalendar(LocalDate d) {
        calendarPage.select(d);
        navigate(2);
    }

    /**
     * Menjalankan aksi ke backend. Kalau backend menolak (misalnya InvalidTaskException),
     * pesannya ditampilkan sebagai peringatan. Setelah itu halaman digambar ulang.
     */
    private void run(Runnable action) {
        try {
            action.run();
        } catch (RuntimeException ex) {
            Alert a = new Alert(Alert.AlertType.WARNING, ex.getMessage());
            a.setHeaderText("Aksi gagal");
            a.initOwner(stage);
            a.showAndWait();
        }
        render();
    }

    // =====================================================================
    // 5. Mencari style.css (classpath dulu, lalu langsung di disk)
    // =====================================================================
    private String findCss() {
        java.net.URL u = getClass().getResource("style.css");
        if (u != null) return u.toExternalForm();

        String[] paths = {
            "todo-tugas-kuliah/src/todo/ui/style.css",   // dijalankan dari folder induk
            "src/todo/ui/style.css"                      // dijalankan dari folder proyek
        };
        for (String p : paths) {
            java.io.File f = new java.io.File(p);
            if (f.exists()) return f.toURI().toString();
        }
        return null;
    }
}
