package todo.model;

/** Prioritas tugas. (Anggota 1) */
public enum Priority {
    HIGHT("Tinggi", 3), MEDIUM("Sedang", 2), LOW("Rendah", 1);

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

    public static Priority fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("Priority label cannot be null or blank");
        }
        for (Priority p : Priority.values()) {
            if (p.getLabel().equalsIgnoreCase(label)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Invalid priority label: " + label);
    }

    @Override
    public String toString() {
        return label;
    }
}
