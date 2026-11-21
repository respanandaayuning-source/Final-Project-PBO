
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

        // Sidebar
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(224);
        sidebar.setMinWidth(224);
        sidebar.setStyle("-fx-background-color: #76944c;");
        
        //mainframe
        VBox center = new VBox();
        center.setStyle("-fx-background-color: #F8F6EE;");

        //agenda
        VBox rightPanel = new VBox();
        rightPanel.setPrefWidth(368);
        rightPanel.setMinWidth(368);
        rightPanel.setStyle("-fx-background-color: #FFFFFF;");

        // Layout utama
        BorderPane root = new BorderPane();
        root.setCenter(sidebar);
        root.setLeft(center);
        root.setRight(rightPanel);


        // Menampilkan jendela
        Scene scene = new Scene(root, 1280, 800);

        stage.setTitle("Manajemen Tugas Kuliah");
        stage.setScene(scene);
        stage.setMinWidth(1280);
        stage.setMinHeight(700);
        stage.show();
    }
}
