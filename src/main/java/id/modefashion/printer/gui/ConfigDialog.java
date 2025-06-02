package id.modefashion.printer.gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.File;
import id.modefashion.printer.util.Helper;

public class ConfigDialog {
    private boolean saved = false;
    private PropertiesConfiguration config;

    public boolean showAndWait(Window owner) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle("Edit Printer Configuration");

        // Load current config
        try {
            config = new PropertiesConfiguration("printer.properties");
        } catch (Exception e) {
            showError("Failed to load configuration: " + e.getMessage());
            return false;
        }

        TextField portField = new TextField(config.getString("printer.port", "20001"));
        ComboBox<String> printerCombo = new ComboBox<>();
        String[] printerNames = Helper.getAllPrinterNames();
        printerCombo.getItems().addAll(printerNames);
        String currentPrinter = config.getString("printer.name", "");
        if (!currentPrinter.isEmpty()) {
            printerCombo.setValue(currentPrinter);
        } else if (printerNames.length > 0) {
            printerCombo.setValue(printerNames[0]);
        }
        ComboBox<String> fontFamilyCombo = new ComboBox<>();
        fontFamilyCombo.getItems().addAll("Font_A_Default", "Font_B", "Font_C");
        String currentFontFamily = config.getString("font.family", "Font_A_Default");
        fontFamilyCombo.setValue(currentFontFamily);
        ComboBox<String> fontSizeCombo = new ComboBox<>();
        fontSizeCombo.getItems().addAll("_1", "_2", "_3", "_4", "_5", "_6", "_7", "_8");
        String currentFontSize = config.getString("font.size", "_1");
        fontSizeCombo.setValue(currentFontSize);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Printer Port:"), 0, 0);
        grid.add(portField, 1, 0);
        grid.add(new Label("Printer Name:"), 0, 1);
        grid.add(printerCombo, 1, 1);
        grid.add(new Label("Font Family:"), 0, 2);
        grid.add(fontFamilyCombo, 1, 2);
        grid.add(new Label("Font Size:"), 0, 3);
        grid.add(fontSizeCombo, 1, 3);

        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");
        saveBtn.setDefaultButton(true);
        cancelBtn.setCancelButton(true);
        grid.add(saveBtn, 0, 4);
        grid.add(cancelBtn, 1, 4);

        saveBtn.setOnAction(e -> {
            // Validate
            String port = portField.getText().trim();
            String name = printerCombo.getValue();
            String fontFamily = fontFamilyCombo.getValue();
            String fontSize = fontSizeCombo.getValue();
            if (!port.matches("\\d+")) {
                showError("Port must be a number.");
                return;
            }
            if (name == null || name.isEmpty()) {
                showError("Printer name cannot be empty.");
                return;
            }
            if (fontFamily == null || fontFamily.isEmpty()) {
                showError("Font family cannot be empty.");
                return;
            }
            if (fontSize == null || !fontSize.matches("_?\\d+")) {
                showError("Font size must be a number, optionally prefixed with _.");
                return;
            }
            // Save
            try {
                config.setProperty("printer.port", port);
                config.setProperty("printer.name", name);
                config.setProperty("font.family", fontFamily);
                config.setProperty("font.size", fontSize);
                config.save(new File("printer.properties"));
                saved = true;
                dialog.close();
            } catch (Exception ex) {
                showError("Failed to save configuration: " + ex.getMessage());
            }
        });
        cancelBtn.setOnAction(e -> dialog.close());

        Scene scene = new Scene(grid);
        dialog.setScene(scene);
        dialog.showAndWait();
        return saved;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 