package todo.ui;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/** Ikon garis sederhana (kotak 24x24) dari path SVG. */
public final class Icons {
    public static final String MENU   = "M3 6h18M3 12h18M3 18h18";
    public static final String SEARCH = "M11 3a8 8 0 1 0 0 16a8 8 0 0 0 0-16z M21 21l-4.35-4.35";
    public static final String BELL   = "M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9 M10.3 21a1.94 1.94 0 0 0 3.4 0";

    private Icons() {
    }

    public static Node icon(String pathData, double size, Color color) {
        SVGPath p = new SVGPath();
        p.setContent(pathData);
        p.setFill(null);
        p.setStroke(color);
        p.setStrokeWidth(2);
        p.setStrokeLineCap(StrokeLineCap.ROUND);
        p.setStrokeLineJoin(StrokeLineJoin.ROUND);
        p.setScaleX(size / 24.0);
        p.setScaleY(size / 24.0);

        StackPane box = new StackPane(new Group(p));
        box.setMinSize(size, size);
        box.setPrefSize(size, size);
        box.setMaxSize(size, size);
        box.setMouseTransparent(true);
        return box;
    }
}