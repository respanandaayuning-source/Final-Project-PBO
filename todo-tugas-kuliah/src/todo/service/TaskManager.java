package todo.service;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;

import todo.exception.InvalidTaskException;
import todo.model.Priority;
import todo.model.Task;
import todo.storage.TaskStorage;

/**
 * Logika inti: tambah, ubah, hapus, cari, filter, sort. (Anggota 1)
 * Nama method adalah KONTRAK dengan GUI (Anggota 2) dan Main (Anggota 3).
 * Jangan ubah signature tanpa kabar-kabar.
 */
public class TaskManager {
    private final List<Task> tasks;
    private final TaskStorage storage;

    public TaskManager() {
        this(null);
    }

    public TaskManager(TaskStorage storage) {
        this.storage = storage;
        this.tasks = new ArrayList<>();
        if (this.storage != null) {
            try {
                List<Task> loaded = this.storage.load();
                if (loaded != null) {
                    this.tasks.addAll(loaded);
                }
            } catch (IOException e) {
                throw new InvalidTaskException("Gagal memuat data tugas: " + e.getMessage(), e);
            }
        }
    }

    public Task addTask(String title, String course, java.time.LocalDateTime deadline, Priority priority, String notes)
            throws InvalidTaskException {
        return addTask(new Task(title, course, deadline, priority, notes));
    }

    /** Validasi (judul kosong, deadline null) lalu tambahkan. */
    public Task addTask(Task task) throws InvalidTaskException {
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            throw new InvalidTaskException("Judul tidak boleh kosong");
        }
        if (task.getDeadline() == null || task.getDeadline().isBefore(java.time.LocalDateTime.now())) {
            throw new InvalidTaskException("Deadline tidak boleh kosong atau sudah lewat");
        }
        if (task.getId() < Task.MIN_ID || task.getId() > Task.MAX_ID) {
            throw new InvalidTaskException("ID tugas tidak valid!");
        }
        tasks.add(task);
        return task;
    }

    public void updateTask(int id, String title, String course, LocalDateTime deadline, Priority priority, String notes,
            boolean done) throws InvalidTaskException {
        Task existing = findById(id);
        if (existing == null) {
            throw new InvalidTaskException("Tugas dengan ID " + id + " tidak ditemukan.");
        }
        existing.setTitle(title);
        existing.setCourse(course);
        existing.setDeadline(deadline);
        existing.setPriority(priority);
        existing.setNotes(notes);
        existing.setDone(done);
        saveChanges();
    }

    public void toggleTaskDone(int id) {
        Task existing = findById(id);
        if (existing == null) {
            throw new InvalidTaskException("Tugas dengan ID " + id + " tidak ditemukan.");
        }
        existing.setDone(!existing.isDone());
        saveChanges();
    }

    public void deleteTask(int id) {
        Task existing = findById(id);
        if (existing == null) {
            throw new InvalidTaskException("Tugas dengan ID " + id + " tidak ditemukan.");
        }
        tasks.remove(existing);
        saveChanges();
    }

    public List<Task> getAll() {
        return new ArrayList<>(tasks);
    }

    //Dipakai Main saat memuat data dari file
    public void setAll(List<Task> loaded) {
        tasks.clear();
        tasks.addAll(loaded);
    }

    public List<Task> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAll();
        }
        String lowerKeyword = keyword.trim().toLowerCase();
        List<Task> result = new ArrayList<>(tasks);
        for (Task t : tasks) {
            boolean matches = t.getTitle().toLowerCase().contains(lowerKeyword)
                    || t.getCourse().toLowerCase().contains(lowerKeyword)
                    || t.getNotes().toLowerCase().contains(lowerKeyword);
            if (matches) {
                result.add(t);
            }
        }
        return result;
    }

    public List<Task> filterByCourse(String course) {
        if (course == null || course.isBlank()) {
            return getAll();
        }
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getCourse().equalsIgnoreCase(course)) {
                result.add(t);
            }
        }
        return result;
    }

    public List<String> getAllCourses() {
        Set<String> Allcourses = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Task t : tasks) {
            if (t.getCourse() != null && !t.getCourse().isBlank()) {
                Allcourses.add(t.getCourse());
            }
        }
        return new ArrayList<>(Allcourses);
    }

    public List<Task> filterByPriority(Priority priority) {
        if (priority == null) {
            return getAll();
        }
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getPriority() == priority) {
                result.add(t);
            }
        }
        return result;
    }

    public List<Task> filterByStatus(boolean done) {
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (t.isDone() == done) {
                result.add(t);
            }
        }
        return result;
    }

    public List<Task> sortByDeadline() {
        List<Task> sorted = new ArrayList<>(tasks);
        sorted.sort(Comparator.comparing(Task::getDeadline));
        return sorted;
    }

    public List<Task> sortByPriority() {
        List<Task> sorted = new ArrayList<>(tasks);
        sorted.sort(Comparator.comparing(Task::getPriority));
        return sorted;
    }

    public Task findById(int id) {
        for (Task t : tasks) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    private void saveChanges() {
        if (storage != null) {
            try {
                storage.save(tasks);
            } catch (IOException e) {
                throw new RuntimeException("Gagal menyimpan data tugas: " + e.getMessage(), e);
            }
        }
    }
}
