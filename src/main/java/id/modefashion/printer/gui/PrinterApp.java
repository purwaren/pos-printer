package id.modefashion.printer.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.LoggerFactory;

public class PrinterApp extends Application {
    private TextArea logArea;
    private Button startStopButton;
    private boolean serverRunning = false;
    private PrintServerController serverController;

    @Override
    public void start(Stage primaryStage) {
        serverController = new PrintServerController();
        // Register log appender
        PrinterLogAppender.setLogArea(logArea);
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        PrinterLogAppender appender = new PrinterLogAppender();
        appender.start();
        rootLogger.addAppender(appender);
        primaryStage.setTitle("MPOS Printer Server");

        // Menu bar
        MenuBar menuBar = new MenuBar();
        Menu configMenu = new Menu("Configuration");
        MenuItem editConfig = new MenuItem("Edit Properties...");
        editConfig.setOnAction(e -> showConfigDialog());
        configMenu.getItems().add(editConfig);
        menuBar.getMenus().add(configMenu);

        // Start/Stop button
        startStopButton = new Button("Start Server");
        startStopButton.setOnAction(e -> toggleServer());
        HBox topBar = new HBox(10, startStopButton);
        topBar.setStyle("-fx-padding: 10;");

        // Log area
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefHeight(400);

        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(logArea);
        root.setBottom(topBar);

        Scene scene = new Scene(root, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void toggleServer() {
        if (serverRunning) {
            boolean stopped = serverController.stopServer();
            if (stopped) {
                appendLog("Server stopped.");
                startStopButton.setText("Start Server");
                serverRunning = false;
            } else {
                showError("Failed to stop server.");
            }
        } else {
            boolean started = serverController.startServer();
            if (started) {
                appendLog("Server started.");
                startStopButton.setText("Stop Server");
                serverRunning = true;
            } else {
                showError("Failed to start server. Check configuration and port availability.");
            }
        }
    }

    private void showConfigDialog() {
        ConfigDialog dialog = new ConfigDialog();
        boolean saved = dialog.showAndWait(startStopButton.getScene().getWindow());
        if (saved) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Configuration Saved");
            alert.setHeaderText(null);
            alert.setContentText("Configuration saved. Please stop and start the server for changes to take effect.");
            alert.showAndWait();
        }
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public void appendLog(String message) {
        logArea.appendText(message + "\n");
        logArea.setScrollTop(Double.MAX_VALUE); // Auto-scroll
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Logback appender for GUI
    public static class PrinterLogAppender extends AppenderBase<ILoggingEvent> {
        private static TextArea logArea;
        public static void setLogArea(TextArea area) {
            logArea = area;
        }
        @Override
        protected void append(ILoggingEvent eventObject) {
            if (logArea != null) {
                javafx.application.Platform.runLater(() -> {
                    logArea.appendText(eventObject.getFormattedMessage() + "\n");
                    logArea.setScrollTop(Double.MAX_VALUE);
                });
            }
        }
    }
} 