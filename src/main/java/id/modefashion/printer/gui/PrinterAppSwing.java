package id.modefashion.printer.gui;

import id.modefashion.printer.util.Helper;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class PrinterAppSwing extends JFrame {
    private JTextArea logArea;
    private JButton startStopButton;
    private PrintServerController serverController;
    private boolean serverRunning = false;

    public PrinterAppSwing() {
        super("MPOS Printer Server");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        serverController = new PrintServerController();

        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(logArea);
        DefaultCaret caret = (DefaultCaret) logArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        // Logback appender
        PrinterLogAppender.setLogArea(logArea);
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        PrinterLogAppender appender = new PrinterLogAppender();
        appender.start();
        rootLogger.addAppender(appender);

        // Start/Stop button
        startStopButton = new JButton("Start Server");
        startStopButton.addActionListener(e -> toggleServer());

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu configMenu = new JMenu("Configuration");
        JMenuItem editConfig = new JMenuItem("Edit Properties...");
        editConfig.addActionListener(e -> showConfigDialog());
        configMenu.add(editConfig);
        menuBar.add(configMenu);
        setJMenuBar(menuBar);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(startStopButton);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
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
                showError("Failed to start server. Check configuration, port availability, and printer availability.");
            }
        }
    }

    private void showConfigDialog() {
        JDialog dialog = new JDialog(this, "Edit Printer Configuration", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        PropertiesConfiguration config;
        try {
            config = new PropertiesConfiguration("printer.properties");
        } catch (Exception e) {
            showError("Failed to load configuration: " + e.getMessage());
            return;
        }

        JTextField portField = new JTextField(config.getString("printer.port", "20001"), 15);
        JComboBox<String> printerCombo = new JComboBox<>(Helper.getAllPrinterNames());
        String currentPrinter = config.getString("printer.name", "");
        if (!currentPrinter.isEmpty()) printerCombo.setSelectedItem(currentPrinter);
        JComboBox<String> fontFamilyCombo = new JComboBox<>(new String[]{"Font_A_Default", "Font_B", "Font_C"});
        fontFamilyCombo.setSelectedItem(config.getString("font.family", "Font_A_Default"));
        JComboBox<String> fontSizeCombo = new JComboBox<>(new String[]{"_1", "_2", "_3", "_4", "_5", "_6", "_7", "_8"});
        fontSizeCombo.setSelectedItem(config.getString("font.size", "_1"));

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Printer Port:"), gbc);
        gbc.gridx = 1; panel.add(portField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Printer Name:"), gbc);
        gbc.gridx = 1; panel.add(printerCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Font Family:"), gbc);
        gbc.gridx = 1; panel.add(fontFamilyCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Font Size:"), gbc);
        gbc.gridx = 1; panel.add(fontSizeCombo, gbc);

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; panel.add(btnPanel, gbc);

        saveBtn.addActionListener(e -> {
            String port = portField.getText().trim();
            String name = (String) printerCombo.getSelectedItem();
            String fontFamily = (String) fontFamilyCombo.getSelectedItem();
            String fontSize = (String) fontSizeCombo.getSelectedItem();
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
            try {
                config.setProperty("printer.port", port);
                config.setProperty("printer.name", name);
                config.setProperty("font.family", fontFamily);
                config.setProperty("font.size", fontSize);
                config.save(new File("printer.properties"));
                JOptionPane.showMessageDialog(this, "Configuration saved. Please stop and start the server for changes to take effect.", "Configuration Saved", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                showError("Failed to save configuration: " + ex.getMessage());
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void appendLog(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PrinterAppSwing app = new PrinterAppSwing();
            app.setVisible(true);
        });
    }

    // Logback appender for GUI
    public static class PrinterLogAppender extends AppenderBase<ILoggingEvent> {
        private static JTextArea logArea;
        public static void setLogArea(JTextArea area) {
            logArea = area;
        }
        @Override
        protected void append(ILoggingEvent eventObject) {
            if (logArea != null) {
                SwingUtilities.invokeLater(() -> {
                    logArea.append(eventObject.getFormattedMessage() + "\n");
                    logArea.setCaretPosition(logArea.getDocument().getLength());
                });
            }
        }
    }
} 