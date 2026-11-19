package todo.service;

import java.util.ArrayList;
import java.util.List;

import todo.model.Task;

/** Mencari tugas yang hampir jatuh tempo / terlambat. (Anggota 1) */
public class ReminderService {
    private final TaskManager manager;

    public ReminderService(TaskManager manager) {
        this.manager = manager;
    }

    /** Tugas belum selesai yang deadline-nya dalam 24 jam ke depan. */
    public List<Task> getDueSoon() {
        List<Task> dueSoon = new ArrayList<>();
        for (Task t : manager.getAll()) {
            if (t.isDueWithinHours(24) && !t.isDone()) {
                dueSoon.add(t);
            }
        }
        return dueSoon;
    }

    /** Tugas belum selesai yang deadline-nya sudah lewat. */
    public List<Task> getOverdue() {
        List<Task> overdue = new ArrayList<>();
        for (Task t : manager.getAll()) {
            if (t.isOverdue() && !t.isDone()) {
                overdue.add(t);
            }
        }
        return overdue;
    }

    public String getAllReminders() {
        List<Task> dueSoon = getDueSoon();
        List<Task> overdue = getOverdue();
        if (dueSoon.isEmpty() && overdue.isEmpty()) {
            return null;
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
