package todo.ui;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import todo.service.ReminderService;
import todo.service.TaskManager;

public class MainFrame {
    private final TaskManager manager;
    private final ReminderService reminder;

    public MainFrame(TaskManager manager, ReminderService reminder) {
        this.manager = manager;
        this.reminder = reminder;
    }

    public void show(Stage stage) {
        stage.setTitle("todolist");
        stage.setScene(new Scene(new Label("todolist jalan"), 600, 400));
        stage.show();
    }
}