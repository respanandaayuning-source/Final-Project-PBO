
package todo.ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import todo.service.ReminderService;
import todo.service.TaskManager;

/** Jendela utama aplikasi JavaFX. (Anggota 2) */
public class MainFrame {

    private final TaskManager manager;
    private final ReminderService reminder;

    public MainFrame(TaskManager manager, ReminderService reminder) {
        this.manager = manager;
        this.reminder = reminder;
    }

    public void show(Stage stage) {

        // Judul aplikasi
        Label title = new Label("Manajemen Tugas Kuliah");
        title.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;"
        );

        // Informasi awal
        Label description = new Label(
            "Selamat datang di aplikasi Manajemen Tugas Kuliah!"
        );

        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        content.getChildren().addAll(title, description);

        // Layout utama
        BorderPane root = new BorderPane();
        root.setCenter(content);

        // Menampilkan jendela
        Scene scene = new Scene(root, 900, 550);

        stage.setTitle("Manajemen Tugas Kuliah");
        stage.setScene(scene);
        stage.setMinWidth(700);
        stage.setMinHeight(450);
        stage.show();
    }
}
