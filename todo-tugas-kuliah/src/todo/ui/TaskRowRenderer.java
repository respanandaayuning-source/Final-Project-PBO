package todo.ui;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/** Pewarnaan baris: merah (terlambat/H-1), kuning (H-3), hijau (selesai). (Anggota 2) */
public class TaskRowRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        // TODO (A2): ambil Task dari TaskTableModel, set warna latar sesuai aturan
        return c;
    }
}
