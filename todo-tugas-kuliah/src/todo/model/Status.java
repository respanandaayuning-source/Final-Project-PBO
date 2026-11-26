package todo.model;

import todo.exception.InvalidTaskException;

public enum Status {
    DONE("Selesai"), IN_PROGRESS("Sedang Dikerjakan"), NOT_DONE("Belum Selesai"), OVERDUE("Terlambat");

    private final String label;

    Status(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Status fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new InvalidTaskException("Label status tidak boleh kosong");
        }
        for (Status s : Status.values()) {
            if (s.getLabel().equalsIgnoreCase(label.trim()) || s.name().equalsIgnoreCase(label.trim())) {
                return s;
            }
        }
        throw new InvalidTaskException("Label status tidak valid: " + label);
    }

    @Override
    public String toString() {
        return label;
    }
}
