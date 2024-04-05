package id.modefashion.printer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.anastaciocintra.output.PrinterOutputStream;

/**
 * MPOS Java Printer
 * 
 * @author: purwaren
 */
public class App {
  private static final Logger logger = LoggerFactory.getLogger(App.class);

  public static void main(String[] args) {
    try {
      PropertiesConfiguration config = new PropertiesConfiguration("printer.properties");
      logger.info("Initialize application....");
      logger.info("Printer use : " + config.getString("printer.name"));

      if (config.getString("printer.name").isEmpty()) {
        logger.info("Usage: java -jar xyz.jar (\"printer name\")");
        logger.info("Printer list to use:");
        String[] printServicesNames = PrinterOutputStream.getListPrintServicesNames();
        for (String printServiceName : printServicesNames) {
          logger.info(printServiceName);
        }

        System.exit(0);
      }

      PrintServer server = new PrintServer(config);
      server.start();
      logger.info("Websocket server is runnig on port {}", config.getInt("printer.port"));
    } catch (ConfigurationException e) {
      e.printStackTrace();
    }
  }
}