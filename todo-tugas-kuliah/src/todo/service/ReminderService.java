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
        // TODO (A1)
        return new ArrayList<>();
    }

    /** Tugas belum selesai yang deadline-nya sudah lewat. */
    public List<Task> getOverdue() {
        // TODO (A1)
        return new ArrayList<>();
    }
}
