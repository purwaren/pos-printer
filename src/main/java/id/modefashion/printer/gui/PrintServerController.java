package id.modefashion.printer.gui;

import id.modefashion.printer.PrintServer;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import id.modefashion.printer.util.Helper;

public class PrintServerController {
    private PrintServer server;
    private Thread serverThread;
    private boolean running = false;
    private final String configFile = "printer.properties";

    public boolean startServer() {
        if (running) return false;
        try {
            PropertiesConfiguration config = new PropertiesConfiguration(configFile);
            String printerName = config.getString("printer.name");
            if (Helper.findPrinterByName(printerName) == null) {
                System.err.println("Printer not found: " + printerName);
                return false;
            }
            server = new PrintServer(config);
            serverThread = new Thread(() -> {
                server.start();
            });
            serverThread.setDaemon(true);
            serverThread.start();
            running = true;
            return true;
        } catch (ConfigurationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean stopServer() {
        if (!running) return false;
        try {
            server.stop();
            serverThread.join(2000); // Wait for thread to finish
            running = false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isRunning() {
        return running;
    }
} 