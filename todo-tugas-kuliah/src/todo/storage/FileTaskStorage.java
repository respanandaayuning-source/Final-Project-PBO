package todo.storage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import todo.model.Priority;
import todo.model.Task;

public class FileTaskStorage implements TaskStorage {
    private static final String HEADER = "id,title,course,deadline,priority,done,notes";
    private static final int COLUMN_COUNT = 7;

    private final Path path;
    private int lastSkippedCount = 0;

    public FileTaskStorage(String filePath) {
        this.path = Paths.get(filePath);
    }

    public int getLastSkippedCount() {
        return lastSkippedCount;
    }

    @Override
    public void save(List<Task> tasks) throws IOException {
        Path parent = path.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(HEADER);
            writer.write("\n");
            for (Task t : tasks) {
                writer.write(toCsvLine(t));
                writer.write("\n");
            }
        }
    }

    private String toCsvLine(Task t) {
        String[] cols = {
            String.valueOf(t.getId()),
            t.getTitle(),
            t.getCourse(),
            t.getDeadline() == null ? "" : t.getDeadline().format(Task.DEADLINE_FORMATTER),
            t.getPriority() == null ? "" : t.getPriority().name(),
            String.valueOf(t.isDone()),
            t.getNotes()
        };
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cols.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(escape(cols[i]));
        }
        return sb.toString();
    }

    private String escape(String value) {
        if (value == null) return "";
        boolean needsQuote = value.contains(",") || value.contains("\"")
                || value.contains("\n") || value.contains("\r");
        if (!needsQuote) return value;
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    @Override
    public List<Task> load() throws IOException {
        lastSkippedCount = 0;
        List<Task> result = new ArrayList<>();

        // File belum ada (pertama kali aplikasi dijalankan) -> bukan error
        if (!Files.exists(path)) {
            return result;
        }

        String text = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        List<List<String>> records = parseCsv(text);

        for (int i = 0; i < records.size(); i++) {
            List<String> row = records.get(i);

            if (i == 0 && !row.isEmpty() && row.get(0).equals("id")) {
                continue; // lewati header
            }
            if (row.size() == 1 && row.get(0).isEmpty()) {
                continue; // baris kosong
            }
            try {
                result.add(toTask(row));
            } catch (RuntimeException e) {
                // IllegalArgumentException mencakup NumberFormatException & Priority.valueOf gagal
                lastSkippedCount++;
                System.err.println("Baris data ke-" + (i + 1) + " dilewati (rusak): " + e.getMessage());
            }
        }
        return result;
    }

    private Task toTask(List<String> row) {
        if (row.size() != COLUMN_COUNT) {
            throw new IllegalArgumentException("jumlah kolom " + row.size() + ", seharusnya " + COLUMN_COUNT);
        }
        int id = Integer.parseInt(row.get(0).trim());
        String title = row.get(1);
        String course = row.get(2);
        LocalDateTime deadline = LocalDateTime.parse(row.get(3).trim(), Task.DEADLINE_FORMATTER);
        Priority priority = Priority.valueOf(row.get(4).trim());
        boolean done = Boolean.parseBoolean(row.get(5).trim());
        String notes = row.get(6);

        return new Task(id, title, course, deadline, priority, notes, done);
    }

    private List<List<String>> parseCsv(String text) {
        List<List<String>> records = new ArrayList<>();
        List<String> row = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        int len = text.length();

        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);

            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < len && text.charAt(i + 1) == '"') {
                        field.append('"'); // "" -> "
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    field.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    row.add(field.toString());
                    field.setLength(0);
                } else if (c == '\n' || c == '\r') {
                    if (c == '\r' && i + 1 < len && text.charAt(i + 1) == '\n') {
                        i++; // CRLF dihitung satu baris baru
                    }
                    row.add(field.toString());
                    field.setLength(0);
                    records.add(row);
                    row = new ArrayList<>();
                } else {
                    field.append(c);
                }
            }
        }

        // Baris terakhir tanpa newline di ujung file
        if (field.length() > 0 || !row.isEmpty()) {
            row.add(field.toString());
            records.add(row);
        }
        return records;
    }
}