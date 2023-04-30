package id.modefashion.printer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MPOS Java Printer
 * 
 * @author: purwaren
 */
public class App {
  private static final Logger logger = LoggerFactory.getLogger(App.class);
  public static void main(String[] args) {

    try {
      logger.info("Initialize application....");
      PropertiesConfiguration config = new PropertiesConfiguration("printer.properties");
      PrintServer server = new PrintServer(config);
      server.start();
      logger.info("Websocket server is runnig on port {}", config.getInt("printer.port"));
    } catch (ConfigurationException e) {
      e.printStackTrace();
    } 
  }
}