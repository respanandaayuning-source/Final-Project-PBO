package todo.model;

import java.time.LocalDate;

/** Data satu tugas kuliah. (Anggota 1) */
public class Task {
    private int id;
    private String title;
    private String course;
    private LocalDate deadline;
    private Priority priority;
    private boolean done;
    private String notes;

    public Task(String title, String course, LocalDate deadline, Priority priority, String notes) {
        this.title = title;
        this.course = course;
        this.deadline = deadline;
        this.priority = priority;
        this.notes = notes;
        this.done = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
