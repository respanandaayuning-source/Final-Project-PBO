package todo.ui;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/** Bar hijau di atas: menu, logo, tiga tab, pencarian, dan lonceng. */
public class NavBar extends HBox {

    public enum Page {
        RINGKASAN("Ringkasan"),
        SEMUA_TUGAS("Semua tugas"),
        KALENDER("Kalender");

        private final String label;

        Page(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private static final Color ICON_COLOR = Color.web("#E6ECD9");

    private final Map<Page, Button> tabs = new EnumMap<>(Page.class);
    private final TextField searchField = new TextField();
    private final Button bellButton = new Button();

    public NavBar(Consumer<Page> onNavigate) {
        getStyleClass().add("nav-bar");
        setAlignment(Pos.CENTER_LEFT);

        Button menuButton = iconButton(Icons.MENU, 22);

        Label logo = new Label("todolist.");
        logo.getStyleClass().addAll("fraunces", "logo");
        HBox.setMargin(logo, new Insets(0, 0, 0, 10));

        HBox tabBox = new HBox(4);
        tabBox.setAlignment(Pos.CENTER_LEFT);
        for (Page page : Page.values()) {
            Button tab = new Button(page.getLabel());
            tab.getStyleClass().add("nav-tab");
            tab.setFocusTraversable(false);
            tab.setOnAction(e -> {
                setActive(page);
                onNavigate.accept(page);
            });
            tabs.put(page, tab);
            tabBox.getChildren().add(tab);
        }
        HBox.setMargin(tabBox, new Insets(0, 0, 0, 44));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField.setPromptText("Cari tugasmu…");
        searchField.getStyleClass().add("search-field");
        HBox searchBox = new HBox(0, Icons.icon(Icons.SEARCH, 18, ICON_COLOR), searchField);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        Region divider = new Region();
        divider.getStyleClass().add("nav-divider");
        HBox.setMargin(divider, new Insets(0, 24, 0, 24));

        bellButton.setGraphic(Icons.icon(Icons.BELL, 22, ICON_COLOR));
        bellButton.getStyleClass().add("nav-icon-btn");
        bellButton.setFocusTraversable(false);

        getChildren().addAll(menuButton, logo, tabBox, spacer, searchBox, divider, bellButton);
    }

    private Button iconButton(String pathData, double size) {
        Button b = new Button();
        b.setGraphic(Icons.icon(pathData, size, ICON_COLOR));
        b.getStyleClass().add("nav-icon-btn");
        b.setFocusTraversable(false);
        return b;
    }

    /** Menandai tab yang sedang aktif. */
    public void setActive(Page page) {
        for (Button b : tabs.values()) {
            b.getStyleClass().remove("active");
        }
        tabs.get(page).getStyleClass().add("active");
    }

    public TextField getSearchField() {
        return searchField;
    }

    public Button getBellButton() {
        return bellButton;
    }
}