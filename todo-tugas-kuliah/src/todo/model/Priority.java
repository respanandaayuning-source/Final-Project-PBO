package todo.model;

import todo.exception.InvalidTaskException;

/** Prioritas tugas. (Anggota 1) */
public enum Priority {
    HIGH("Tinggi", 3), MEDIUM("Sedang", 2), LOW("Rendah", 1);

    private final String label;
    private final int level;

    Priority(String label, int level) {
        this.label = label;
        this.level = level;
    }

    public String getLabel() {
        return label;
    }

    public int getLevel() {
        return level;
    }
    
    public static Priority fromLevel(int level) {
        for (Priority p : Priority.values()) {
            if (p.getLevel() == level) {
                return p;
            }
        }
        throw new InvalidTaskException("Level prioritas tidak valid: " + level);
    }

    public static Priority fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new InvalidTaskException("Label prioritas tidak boleh kosong");
        }
        for (Priority p : Priority.values()) {
            if (p.getLabel().equalsIgnoreCase(label.trim()) || p.name().equalsIgnoreCase(label.trim())) {
                return p;
            }
        }
        throw new InvalidTaskException("Label prioritas tidak valid: " + label);
    }

    @Override
    public String toString() {
        return label;
    }
}
