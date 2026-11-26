package todo.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import todo.exception.InvalidTaskException;

public class Task {
    public static final DateTimeFormatter DEADLINE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final int MIN_ID = 0;
    public static final int MAX_ID = 0x0FFFFFFF;
    private int id;
    private String title;
    private String course;
    private LocalDateTime deadline;
    private Priority priority;
    private boolean done;
    private String notes;

    private final List<SubTask> subTasks = new ArrayList<>();

    public Task(String title, String course, LocalDateTime deadline, Priority priority, String notes) {
        this.id = generateId();
        setTitle(title);
        setCourse(course);
        setDeadline(deadline);
        setPriority(priority);
        setNotes(notes);
        this.done = false;
    }

    public static int generateId() {
        return Integer.parseInt(UUID.randomUUID().toString().substring(0, 7), 16);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new InvalidTaskException("Judul tidak boleh kosong");
        }
        this.title = title;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = (course != null) ? course.trim() : "";
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        if (deadline == null || deadline.isBefore(LocalDateTime.now())) {
            throw new InvalidTaskException("Deadline tidak boleh kosong atau sudah lewat");
        }
        this.deadline = deadline;
    }

    public String getFormattedDeadline() {
        return deadline.format(DEADLINE_FORMATTER);
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = (priority != null) ? priority : Priority.MEDIUM;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = (notes == null) ? "" : notes.trim();
    }

    public boolean isDone() {
        return done;
    }

    // berhubungan sm subtask
    public void setDone(boolean done) {
        this.done = done;
        if (done) {
            for (SubTask st : subTasks) {
                st.setDone(true);
            }
        }
    }

    public boolean isInProgress() {
        if(!done){
            for (SubTask st : subTasks) {
                if (st.isDone()) return true;
            }
        }
        return false;
    }

    // manajemen subtask
    public void syncStatusFromSubTasks() {
        if (subTasks.isEmpty()) {
            return;
        }
        boolean allCompleted = true;
        for (SubTask st : subTasks) {
            if (!st.isDone()) {
                allCompleted = false;
                break;
            }
        }
        this.done = allCompleted;
    }

    public List<SubTask> getSubTasks() {
        return Collections.unmodifiableList(subTasks);
    }

    public void addSubTask(SubTask subTask) {
        if (subTask == null || subTask.getTitle().isBlank()) {
            throw new InvalidTaskException("Subtask tidak boleh kosong");
        }
        subTasks.add(subTask);
        syncStatusFromSubTasks();
    }

    public void removeSubTask(int index) {
        if (index < 0 || index >= subTasks.size()) {
            throw new IndexOutOfBoundsException("Indeks subtask tidak valid");
        }
        subTasks.remove(index);
        syncStatusFromSubTasks();
    }   

    public void toggleSubTaskDone(int index) {
        if (index < 0 || index >= subTasks.size()) {
            throw new IndexOutOfBoundsException("Indeks subtask tidak valid");
        }
        SubTask st = subTasks.get(index);
        st.setDone(!st.isDone());
        syncStatusFromSubTasks();
    }

    public double getCompletionPercentage() {
        if (subTasks.isEmpty()) {
            return done ? 100.0 : 0.0;
        }
        int completedCount = 0;
        for (SubTask st : subTasks) {
            if (st.isDone()) {
                completedCount++;
            }
        }
        return (completedCount * 100.0) / subTasks.size();
    }

    // status
    public Status getStatus() {
        if (done) {
            return Status.DONE;
        } else if (isInProgress()) {
            return Status.IN_PROGRESS;
        } else if (isOverdue()) {
            return Status.OVERDUE;
        } else {
            return Status.NOT_DONE;
        }
    }

    public boolean isOverdue() {
        return !done && deadline != null && LocalDateTime.now().isAfter(deadline);
    }

    public boolean isDueWithinHours(long hours) {
        if (done || deadline == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return !now.isAfter(deadline) && now.plusHours(hours).isAfter(deadline);
    }

    @Override
    public String toString() {
        return String.format("[%07s] %s (%s) - %s | %s", id, title, course, getFormattedDeadline(), getStatus().getLabel());
    }
}
