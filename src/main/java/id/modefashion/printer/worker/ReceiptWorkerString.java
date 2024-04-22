package id.modefashion.printer.worker;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import id.modefashion.printer.util.Helper;

import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class ReceiptWorkerString {

  private String data;
  private PropertiesConfiguration config;
  private static final Logger logger = LoggerFactory.getLogger(ReceiptWorkerString.class);

  public ReceiptWorkerString(String data, PropertiesConfiguration config) {
    this.data = data;
    this.config = config;
  }

  public void proceed() {
    PrinterJob job = PrinterJob.getPrinterJob();
    try {
      job.setPrintService(Helper.findPrinterByName(config.getString("printer.name")));
      logger.info("Printer Name: {}", config.getString("printer.name"));
      PageFormat pf = job.defaultPage();
      int totalLine = data.split("#").length;
      logger.info("totalLine: {}", totalLine);
      PosReceiptString printable = new PosReceiptString(data, config);
      printable.print(null, pf, 0);
    } catch (PrinterException e) {
      e.printStackTrace();
    }
  }
}
