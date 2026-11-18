package todo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

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

    public Task(int id, String title, String course, LocalDateTime deadline, Priority priority, String notes, boolean done) {   
        this.id = id;
        this.title = title;
        this.course = course;
        this.deadline = deadline;
        this.priority = (priority != null) ? priority : Priority.MEDIUM;
        this.notes = (notes != null) ? notes : "";
        this.done = done;
    }

    public static int generateId() {
        return Integer.parseInt(UUID.randomUUID().toString().substring(0, 7), 16);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
