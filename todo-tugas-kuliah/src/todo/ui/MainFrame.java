
package todo.ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import todo.service.ReminderService;
import todo.service.TaskManager;

import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;



/** Jendela utama aplikasi JavaFX. (Anggota 2) */
public class MainFrame {

    private final TaskManager manager;
    private final ReminderService reminder;

    public MainFrame(TaskManager manager, ReminderService reminder) {
        this.manager = manager;
        this.reminder = reminder;
    }

    public void show(Stage stage) {

        // Sidebar
        VBox sidebar = buildSidebar();
        
        //mainframe
        VBox center = buildCenter();

        //agenda
        VBox rightPanel = new VBox();
        rightPanel.setPrefWidth(368);
        rightPanel.setMinWidth(368);
        rightPanel.setStyle("-fx-background-color: #FFFFFF;");

        // Layout utama
        BorderPane root = new BorderPane();
        root.setCenter(center);
        root.setLeft(sidebar);
        root.setRight(rightPanel);


        // Menampilkan jendela
        Scene scene = new Scene(root, 1280, 800);
        String css = findCss();
        if (css != null) {
            scene.getStylesheets().add(css);
        } else {
        System.err.println("Gagal memuat file CSS: style.css");
        }

        stage.setTitle("Manajemen Tugas Kuliah");
        stage.setScene(scene);
        stage.setMinWidth(1100);
        stage.setMinHeight(500);
        stage.show();
    }


    private VBox  buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(224);
        sidebar.setMinWidth(224);

        // Judul sidebar
        Label logo = new Label("Tugas Kuliah");
        logo.getStyleClass().add("logo-text");
        Label caption = new Label("Aplikasi manajemen tugas kuliah");
        caption.getStyleClass().add("sidebar-caption");
        VBox brand = new VBox (2, logo, caption);

        //menu togelbUtton
        ToggleGroup grup = new ToggleGroup();
        VBox menu = new VBox(8);
        for(String name : new String[]{"Dashboard","Semua Tugas","Kalender", "Statistik"}){
            ToggleButton b = new ToggleButton(name);
            b.setToggleGroup(grup);
            b.getStyleClass().add("menu-item");
            b.setMaxWidth(Double.MAX_VALUE);
            b.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {if(b.isSelected()) e.consume();});
            menu.getChildren().add(b);
        }
        ((ToggleButton)menu.getChildren().get(0)).setSelected(true);

        //Dftar matakuliah
        Label courseTitle = new Label("Matakuliah");
        courseTitle.getStyleClass().add("sidebar-caption");
        VBox courseList = new VBox(14);
        String[] dotcolor = new String[]{"#FFd21F","#C0B6AC","#0000FF","#C8DAA6","#FBF5DB"};
        int i = 0;
        for (String course : manager.getAll().stream().map(t -> t.getCourse()).distinct().sorted().toList()) {
            Circle dot = new Circle(6, Color.web(dotcolor[i % dotcolor.length]));
            Label name = new Label(course);
            name.getStyleClass().add("course-name");
            courseList.getChildren().add(new HBox(8, dot, name));
            i++;
        }

        sidebar.getChildren().addAll(brand, menu, courseTitle, courseList);
        return sidebar;


        
    }

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

    private VBox buildCenter() {
    VBox center = new VBox(24);
    center.getStyleClass().add("center-area");

    // Data untuk kartu dan sapaan (semua dari backend)
    var tasks = manager.getAll();
    long total   = tasks.size();
    long done    = tasks.stream().filter(t -> t.isDone()).count();
    long pending = total - done;
    long soon    = reminder.getDueSoon().size();   // deadline dekat
    long today   = tasks.stream()
            .filter(t -> !t.isDone() && t.getDeadline().toLocalDate().equals(java.time.LocalDate.now()))
            .count();
            
     // Sapaan di kiri
    Label welcome = new Label("SELAMAT DATANG KEMBALI");
    welcome.getStyleClass().add("welcome-caption");
    Label headline = new Label("Bikin hari ini lebih terarah.");
    headline.getStyleClass().add("headline");
    Label sub = new Label("Ada " + today + " tugas yang perlu perhatianmu hari ini.");
    sub.getStyleClass().add("page-sub");
    VBox greeting = new VBox(4, welcome, headline, sub);

    // Tombol di kanan; spacer mendorong tombol ke ujung kanan
    Button add = new Button("+  Tambah tugas");
    add.getStyleClass().add("btn-primary");
    add.setOnAction(e -> System.out.println("TODO: buka form tambah tugas"));
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    HBox header = new HBox(greeting, spacer, add);
    header.setAlignment(Pos.CENTER_LEFT);

    // Empat kartu angka, sama lebar
    HBox stats = new HBox(16,
            statCard(total,   "Total tugas",    "stat-card"),
            statCard(pending, "Belum selesai",  "stat-card"),
            statCard(done,    "Selesai",        "stat-card-done"),
            statCard(soon,    "Deadline dekat", "stat-card-soon"));

    center.getChildren().addAll(header, stats);
    return center;
}

/** Satu kartu angka: angka besar + label kecil di sampingnya. */
private HBox statCard(long number, String label, String styleClass) {
    Label n = new Label(String.valueOf(number));
    n.getStyleClass().add("stat-number");
    Label l = new Label(label);
    l.getStyleClass().add("stat-label");

    HBox card = new HBox(12, n, l);
    card.getStyleClass().addAll("stat-card", styleClass);
    card.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(card, Priority.ALWAYS);   
    return card;
}

}
