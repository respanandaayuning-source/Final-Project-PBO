package todo.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import todo.model.Task;

/** Simpan/muat tugas ke file CSV. (Anggota 3) */
public class FileTaskStorage implements TaskStorage {
    private final String filePath;

    public FileTaskStorage(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void save(List<Task> tasks) throws IOException {
        // TODO (A3): tulis ke filePath (CSV)
    }

    @Override
    public List<Task> load() throws IOException {
        // TODO (A3): baca filePath; jika file belum ada, kembalikan list kosong
        return new ArrayList<>();
    }
}
