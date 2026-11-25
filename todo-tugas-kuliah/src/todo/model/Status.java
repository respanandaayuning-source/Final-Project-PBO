package todo.model;

public enum Status {
    DONE("Selesai"), IN_PROGRESS("Sedang Dikerjakan"), NOT_DONE("Belum Selesai"), LATE("Terlambat");

    private final String label;

    Status(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
