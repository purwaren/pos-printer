package id.modefashion.printer.worker;

import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import id.modefashion.printer.dto.ReceiptLineData;
import id.modefashion.printer.util.Helper;

import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class ReceiptWorker {

  private List<ReceiptLineData> data;
  private PropertiesConfiguration config;
  private static final Logger logger = LoggerFactory.getLogger(ReceiptWorker.class);

  public ReceiptWorker(List<ReceiptLineData> data, PropertiesConfiguration config) {
    this.data = data;
    this.config = config;
  }

  public void proceed() {
    PrinterJob job = PrinterJob.getPrinterJob();
    try {
      job.setPrintService(Helper.findPrinterByName(config.getString("printer.name")));
      logger.info("Printer Name: {}", config.getString("printer.name"));
      PageFormat pf = job.defaultPage();
      int totalLine = data.size();
      logger.info("totalLine: {}", totalLine);
      job.setPrintable(new PosReceipt(data, config), pf);
      job.print();
    } catch (PrinterException e) {
      e.printStackTrace();
    }
  }
}
