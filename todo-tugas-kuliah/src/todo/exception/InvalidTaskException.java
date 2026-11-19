package todo.exception;

/** Custom exception untuk data tugas tidak valid. (Anggota 1) */
public class InvalidTaskException extends Exception {
    public InvalidTaskException(String message) {
        super(message);
    }
    public InvalidTaskException(String message, Throwable cause) {
        super(message, cause);
    }
    public InvalidTaskException() {
        super("Data tugas tidak valid.");
    }

}
