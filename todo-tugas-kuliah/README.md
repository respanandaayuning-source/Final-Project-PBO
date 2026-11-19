# Aplikasi Manajemen Tugas Kuliah dengan Pengingat Deadline

Final Project Java (GUI Swing) - Kelompok ___

## Anggota & peran
| Anggota | Peran | Folder utama |
|---|---|---|
| Phoebe | Logika backend | `model/`, `exception/`, `service/TaskManager`, `service/ReminderService` |
| Respananda | GUI | `ui/` |
| Lucyana | Penyimpanan, integrasi, dokumentasi | `storage/`, `Main`, `service/TaskStatistics` |

## Struktur
```
src/todo/
  model/       Task, Priority
  exception/   InvalidTaskException
  service/     TaskManager, ReminderService, TaskStatistics
  storage/     TaskStorage (interface), FileTaskStorage
  ui/          MainFrame, TaskDialog, TaskTableModel, TaskRowRenderer
  Main.java
data/          file penyimpanan (tasks.csv dibuat otomatis)
docs/          makalah, ppt, screenshots
```

## Cara menjalankan
Butuh JDK 11+.
```
./build.sh
java -jar todo-app.jar
```
Atau jalankan `todo.Main` dari IDE (set working directory ke root proyek).

## Aturan bersama
- Format tanggal: `dd-MM-yyyy`
- Satu orang = satu branch (`fitur-logika`, `fitur-gui`, `fitur-storage`), gabung lewat pull request
- Jangan ubah signature method kontrak tanpa kabar ke anggota lain
