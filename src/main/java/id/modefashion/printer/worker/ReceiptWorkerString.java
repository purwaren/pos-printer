package id.modefashion.printer.worker;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import id.modefashion.printer.paper.ReceiptPaper;
import id.modefashion.printer.util.Helper;

import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class ReceiptWorkerString {

  private String data;
  private PropertiesConfiguration config;
  private static final Logger logger = LoggerFactory.getLogger(ReceiptWorker.class);

  public ReceiptWorkerString(String data, PropertiesConfiguration config) {
    this.data = data;
    this.config = config;
  }

  public void proceed() {
    PrinterJob job = PrinterJob.getPrinterJob();
    try {
      job.setPrintService(Helper.findPrinterByName(config.getString("printer.name")));
      PageFormat pf = job.defaultPage();
      pf.setOrientation(config.getInt("paper.orientation"));
      int totalLine = data.split("#").length;
      logger.info("totalLine: {}", totalLine);
      pf.setPaper(new ReceiptPaper(config, totalLine));
      job.setPrintable(new PosReceiptString(data, config), pf);
      job.print();
    } catch (PrinterException e) {
      e.printStackTrace();
    }
  }
}
