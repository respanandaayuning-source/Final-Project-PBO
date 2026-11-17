package todo.ui;

import java.awt.Frame;
import javax.swing.JDialog;

import todo.model.Task;

/** Form tambah/ubah tugas. (Anggota 2) */
public class TaskDialog extends JDialog {
    private Task result;

    public TaskDialog(Frame owner, Task existing) {
        super(owner, existing == null ? "Tambah Tugas" : "Ubah Tugas", true);
        // TODO (A2): JTextField, JComboBox prioritas, input tanggal, tombol Simpan/Batal
    }

    /** Null jika dibatalkan. */
    public Task getResult() { return result; }
}
