package todo.model;

import todo.exception.InvalidTaskException;

public class SubTask {
    private String title;
    private boolean done;

    public SubTask(String title) {
        setTitle(title);
        this.done = false;
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

    public boolean isDone() {
        return done;
    }
    public void setDone(boolean done) {
        this.done = done;
    }

    @Override 
    public String toString() {
        return (done ? "[v] " : "[ ] ") + title;
    }
}
