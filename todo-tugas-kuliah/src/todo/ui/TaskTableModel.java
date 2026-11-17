package todo.ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

import todo.model.Task;

/** Penghubung data Task ke JTable. (Anggota 2) */
public class TaskTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {"Judul", "Mata Kuliah", "Deadline", "Prioritas", "Status"};
    private List<Task> tasks = new ArrayList<>();

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        fireTableDataChanged();
    }

    public Task getTaskAt(int row) { return tasks.get(row); }

    @Override public int getRowCount() { return tasks.size(); }
    @Override public int getColumnCount() { return COLUMNS.length; }
    @Override public String getColumnName(int c) { return COLUMNS[c]; }

    @Override
    public Object getValueAt(int row, int col) {
        Task t = tasks.get(row);
        switch (col) {
            case 0: return t.getTitle();
            case 1: return t.getCourse();
            case 2: return t.getDeadline();
            case 3: return t.getPriority();
            case 4: return t.isDone() ? "Selesai" : "Belum";
            default: return "";
        }
    }
}
