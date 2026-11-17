package todo.storage;

import java.io.IOException;
import java.util.List;

import todo.model.Task;

/** Kontrak penyimpanan data. (Anggota 3) */
public interface TaskStorage {
    void save(List<Task> tasks) throws IOException;
    List<Task> load() throws IOException;
}
