package todo.exception;

/** Custom exception untuk data tugas tidak valid. (Anggota 1) */
public class InvalidTaskException extends Exception {
    public InvalidTaskException(String message) {
        super(message);
    }
}
