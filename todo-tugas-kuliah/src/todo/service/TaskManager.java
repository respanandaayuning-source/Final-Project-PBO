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
import todo.model.Status;
import todo.model.Task;
import todo.model.SubTask;
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

    // method utama
    public Task addTask(String title, String course, java.time.LocalDateTime deadline, Priority priority, String notes)
            throws InvalidTaskException {
        return addTask(new Task(title, course, deadline, priority, notes));
    }

    public Task addTask(Task task) {
        if (task == null) {
            throw new InvalidTaskException("Objek task tidak boleh kosong");
        }
        Task taskToAdd = task;
        while (findById(taskToAdd.getId()) != null) {
            taskToAdd = new Task(
                    task.getTitle(), task.getCourse(), task.getDeadline(), task.getPriority(), task.getNotes());
            for (SubTask st : task.getSubTasks()) {
                taskToAdd.addSubTask(new SubTask(st.getTitle()));
            }
        }
        tasks.add(task);
        saveChanges();
        return task;
    }

    public void updateTask(int id, String title, String course, LocalDateTime deadline, Priority priority, String notes,
            boolean done) {
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

    public void deleteTask(int id) {
        Task existing = findById(id);
        if (existing == null) {
            throw new InvalidTaskException("Tugas dengan ID " + id + " tidak ditemukan.");
        }
        tasks.remove(existing);
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

    public Task findById(int id) {
        for (Task t : tasks) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public List<Task> getAll() {
        return new ArrayList<>(tasks);
    }

    // manajemen subtask
    public SubTask addSubTask(int taskId, String stTitle) {
        Task task = findById(taskId);
        if (task == null) {
            throw new InvalidTaskException("Tugas dengan ID " + taskId + " tidak ditemukan.");
        }
        SubTask newSt = new SubTask(stTitle);
        task.addSubTask(newSt);
        saveChanges();
        return newSt;
    }

    public void toggleSubTaskDone(int taskId, int index) {
        Task task = findById(taskId);
        if (task == null) {
            throw new InvalidTaskException("Tugas dengan ID " + taskId + " tidak ditemukan.");
        }
        task.toggleSubTaskDone(index);
        saveChanges();
    }

    public void deleteSubTask(int taskId, int index) {
        Task task = findById(taskId);
        if (task == null) {
            throw new InvalidTaskException("Tugas dengan ID " + taskId + " tidak ditemukan.");
        }
        task.removeSubTask(index);
        saveChanges();
    }

    // fitur search 
    private boolean matchesKeyword(Task task, String lowerKeyword) {
        for (Task t : tasks) {
            boolean matches = t.getTitle().toLowerCase().contains(lowerKeyword)
                    || t.getCourse().toLowerCase().contains(lowerKeyword)
                    || t.getNotes().toLowerCase().contains(lowerKeyword);
            if (matches) {
                return true;
            }
        }
        for (SubTask st : task.getSubTasks()) {
            if (st.getTitle().toLowerCase().contains(lowerKeyword)) {
                return true;
            }
        }
        return false;
    }

    public List<Task> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAll();
        }
        String lowerKeyword = keyword.trim().toLowerCase();
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (matchesKeyword(t, lowerKeyword)) {
                result.add(t);
            }
        }
        return result;
    }

    // fitur filter
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

    public List<Task> filterByStatus(Status status) {
        if (status == null) {
            return getAll();
        }
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getStatus() == status) {
                result.add(t);
            }
        }
        return result;
    }

    // sorting 
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

    public List<Task> getInProgressTasks() {
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (t.isInProgress()) {
                result.add(t);
            }
        }
        return result;
    }

    public List<Task> getOverdueTasks() {
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (t.isOverdue()) {
                result.add(t);
            }
        }
        return result;
    }

    // dropdown
    public List<String> getAllCourses() {
        Set<String> Allcourses = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Task t : tasks) {
            if (t.getCourse() != null && !t.getCourse().isBlank()) {
                Allcourses.add(t.getCourse());
            }
        }
        return new ArrayList<>(Allcourses);
    }

    // penyimpanan
    private void saveChanges() {
        if (storage != null) {
            try {
                storage.save(tasks);
            } catch (IOException e) {
                throw new RuntimeException("Gagal menyimpan data tugas: " + e.getMessage(), e);
            }
        }
    }

    public void setAll(List<Task> list) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setAll'");
    }
}
