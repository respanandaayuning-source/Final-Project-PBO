package todo.ui;

import java.io.InputStream;

import javafx.scene.image.Image;

/** Memuat gambar dari src/main/resources/images. Pakai: Assets.image("banner-dashboard.png") */
public final class Assets {
    private Assets() {
    }

    public static Image image(String name) {
        InputStream in = Assets.class.getResourceAsStream("/images/" + name);
        if (in == null) {
            throw new IllegalStateException("Gambar tidak ditemukan di /images/" + name);
        }
        return new Image(in);
    }
}