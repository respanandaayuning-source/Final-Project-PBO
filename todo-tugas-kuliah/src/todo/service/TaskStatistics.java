package todo.service;

import java.util.List;

import todo.model.Task;

/** Ringkasan jumlah tugas selesai, tertunda, terlambat. (Anggota 3) */
public class TaskStatistics {
    public static int countDone(List<Task> tasks) {
        if (tasks == null) return 0;
        int count = 0;
        for (Task t : tasks) {
            if (t.isDone()) {
                count++;
            }
        } 
        return count;
    }

    public static int countPending(List<Task> tasks) {
        if (tasks == null) return 0;
        int count = 0;
        for (Task t : tasks) {
            if (!t.isDone() && !t.isOverdue()) {
                count++;
            }
        }
        return count;
    }

    public static int countOverdue(List<Task> tasks) {
        if (tasks == null) return 0;
        int count = 0;
        for (Task t : tasks) {
            if (t.isOverdue()) {
                count++;
            }
        }
        return count;
    }

    public static String summary(List<Task> tasks) {
        return "Selesai: " + countDone(tasks) + " | Tertunda: " + countPending(tasks) + " | Terlambat: " + countOverdue(tasks);
    }
}
