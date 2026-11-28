package todo.ui;

import java.io.InputStream;

import javafx.scene.text.Font;

/** Memuat font dari src/main/resources/fonts. Panggil Fonts.load() sekali sebelum membuat scene. */
public final class Fonts {
    private static final String[] FILES = {
        "Fraunces-Regular.ttf",
        "DMSans-Regular.ttf",
        "DMSans-Medium.ttf",
        "DMSans-Bold.ttf"
    };

    private Fonts() {
    }

    public static void load() {
        for (String file : FILES) {
            InputStream in = Fonts.class.getResourceAsStream("/fonts/" + file);
            if (in == null) {
                System.err.println("Font tidak ditemukan: /fonts/" + file);
                continue;
            }
            Font font = Font.loadFont(in, 14);
            // Cek nama family yang tercetak; nama itulah yang dipakai di todolist.css
            System.out.println("Font dimuat: " + file + " -> family=\"" + font.getFamily() + "\"");
        }
    }
}