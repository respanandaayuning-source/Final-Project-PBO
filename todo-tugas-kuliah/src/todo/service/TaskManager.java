package todo.service;

import java.util.ArrayList;
import java.util.List;

import todo.exception.InvalidTaskException;
import todo.model.Priority;
import todo.model.Task;

/**
 * Logika inti: tambah, ubah, hapus, cari, filter, sort. (Anggota 1)
 * Nama method adalah KONTRAK dengan GUI (Anggota 2) dan Main (Anggota 3).
 * Jangan ubah signature tanpa kabar-kabar.
 */
public class TaskManager {
    private final List<Task> tasks = new ArrayList<>();

    /** Validasi (judul kosong, deadline null) lalu tambahkan. */
    public void addTask(Task task) throws InvalidTaskException {
        // TODO (A1): validasi, set id, tambahkan ke list
        throw new UnsupportedOperationException("belum diimplementasi");
    }

    public void updateTask(Task task) throws InvalidTaskException {
        // TODO (A1)
        throw new UnsupportedOperationException("belum diimplementasi");
    }

    public void deleteTask(int id) {
        // TODO (A1)
    }

    public List<Task> getAll() {
        return new ArrayList<>(tasks);
    }

    /** Dipakai Main saat memuat data dari file. */
    public void setAll(List<Task> loaded) {
        tasks.clear();
        tasks.addAll(loaded);
    }

    public List<Task> search(String keyword) {
        // TODO (A1)
        return new ArrayList<>();
    }

    public List<Task> filterByCourse(String course) {
        // TODO (A1)
        return new ArrayList<>();
    }

    public List<Task> filterByPriority(Priority priority) {
        // TODO (A1)
        return new ArrayList<>();
    }

    public List<Task> filterByStatus(boolean done) {
        // TODO (A1)
        return new ArrayList<>();
    }

    public List<Task> sortByDeadline() {
        // TODO (A1): pakai Comparator
        return new ArrayList<>();
    }
}
