package todo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

import todo.exception.InvalidTaskException;
import todo.model.Task;

public class ReminderService {
    private final TaskManager manager;

    public ReminderService(TaskManager manager) {
        if (manager == null) {
            throw new InvalidTaskException("Task Manager tidak boleh kosong");
        }
        this.manager = manager;
    }

    // tugas belum selesai dgn deadline dalam 24 jam ke depan
    public List<Task> getDueSoon() {
        List<Task> dueSoon = new ArrayList<>();
        for (Task t : manager.getAll()) {
            if (t.isDueWithinHours(24) && !t.isDone()) {
                dueSoon.add(t);
            }
        }
        dueSoon.sort(Comparator.comparing(Task::getDeadline));
        return dueSoon;
    }

    // tugas deadline sudah lewat
    public List<Task> getOverdue() {
        List<Task> overdue = manager.getOverdueTasks();
        overdue.sort(Comparator.comparing(Task::getDeadline));
        return overdue;
    }

    // semua reminder
    public String getAllReminders() {
        List<Task> dueSoon = getDueSoon();
        List<Task> overdue = getOverdue();
        if (dueSoon.isEmpty() && overdue.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (!dueSoon.isEmpty()) {
            sb.append("Tugas dalam 24 jam ke depan:\n");
            for (Task t : dueSoon) {
                sb.append("- ").append(t.getTitle()).append(" (Deadline: ")
                        .append(t.getDeadline().format(Task.DEADLINE_FORMATTER)).append(")\n");
            }
        }
        if (!overdue.isEmpty()) {
            if (!dueSoon.isEmpty()) {
                sb.append("\n");
            }
            sb.append("Tugas yang terlambat:\n");
            for (Task t : overdue) {
                sb.append("- ").append(t.getTitle()).append(" (Deadline: ")
                        .append(t.getDeadline().format(Task.DEADLINE_FORMATTER)).append(")\n");
            }
        }
        return sb.toString().trim();
    }
}
