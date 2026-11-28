package todo.ui;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import todo.service.ReminderService;
import todo.service.TaskManager;
import todo.ui.NavBar.Page;

/** Jendela utama: nav bar di atas, halaman yang aktif di bawahnya. */
public class MainFrame {
    private static final double DESIGN_WIDTH = 1440;
    private static final double DESIGN_HEIGHT = 1024;

    private final TaskManager manager;
    private final ReminderService reminder;

    private final StackPane content = new StackPane();
    private final Map<Page, Node> pages = new EnumMap<>(Page.class);
    private NavBar navBar;

    public MainFrame(TaskManager manager, ReminderService reminder) {
        this.manager = manager;
        this.reminder = reminder;
    }

    public void show(Stage stage) {
        Fonts.load();

        navBar = new NavBar(this::showPage);

        // Halaman sementara; ganti satu per satu dengan halaman asli
        pages.put(Page.RINGKASAN, placeholder("Ringkasan"));
        pages.put(Page.SEMUA_TUGAS, placeholder("Semua tugas"));
        pages.put(Page.KALENDER, placeholder("Kalender"));

        VBox root = new VBox(32, navBar, content);
        root.setPadding(new Insets(28, 40, 28, 40));
        VBox.setVgrow(content, Priority.ALWAYS);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);

        // Ukuran desain 1440x1024, dibatasi supaya muat di layar laptop
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        double w = Math.min(DESIGN_WIDTH, screen.getWidth() - 20);
        double h = Math.min(DESIGN_HEIGHT, screen.getHeight() - 60);
        Scene scene = new Scene(scroll, w, h);

        URL css = getClass().getResource("/css/todolist.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.err.println("todolist.css tidak ditemukan di /css/ (cek folder src/main/resources/css)");
        }

        showPage(Page.RINGKASAN);

        stage.setTitle("todolist");
        stage.setScene(scene);
        stage.show();
    }

    private void showPage(Page page) {
        navBar.setActive(page);
        content.getChildren().setAll(pages.get(page));
    }

    /** Tampilan sementara; juga untuk mengecek font Fraunces dan DM Sans sudah termuat. */
    private Node placeholder(String title) {
        Label heading = new Label(title);
        heading.getStyleClass().addAll("fraunces", "page-title");
        Label sample = new Label("Contoh teks DM Sans Medium 14 px");
        sample.getStyleClass().addAll("dm-medium", "s14");
        return new VBox(8, heading, sample);
    }
}