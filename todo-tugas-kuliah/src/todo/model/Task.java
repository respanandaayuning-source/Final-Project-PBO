package todo.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import todo.exception.InvalidTaskException;

/** Data satu tugas kuliah. (Anggota 1) */
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

    //buat constructor yg masukin dr UI
    public Task(String title, String course, LocalDateTime deadline, Priority priority, String notes) {
        this(
            generateId(),
            title,
            course,
            deadline,
            priority,
            notes,
            false
        );
    }

    //buat constructor yg masukin dr file data/tasks.csv
    public Task(int id, String title, String course, LocalDateTime deadline, Priority priority, String notes, boolean done) {   
        if (id < MIN_ID || id > MAX_ID) {
            throw new InvalidTaskException("ID tugas harus berada di antara " + MIN_ID + " dan " + MAX_ID + ".");
        }
        this.id = id;
        this.title = title;
        this.course = (course != null) ? course.trim() : "";
        this.deadline = deadline;
        this.priority = (priority != null) ? priority : Priority.MEDIUM;
        this.notes = (notes != null) ? notes.trim() : "";
        this.done = done;
    }

    public static int generateId() {
        return Integer.parseInt(UUID.randomUUID().toString().substring(0, 7), 16);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { 
        if (title == null || title.isBlank()) {
            throw new InvalidTaskException("Judul tidak boleh kosong");
        }
        this.title = title; 
    }

    public String getCourse() { return course; }
    public void setCourse(String course) { 
        this.course = (course != null) ? course.trim() : ""; 
    }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { 
        if (deadline == null || deadline.isBefore(LocalDateTime.now())) {
            throw new InvalidTaskException("Deadline tidak boleh kosong atau sudah lewat");
        }
        this.deadline = deadline; 
    }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = (priority != null) ? priority : Priority.MEDIUM; }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }
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

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
