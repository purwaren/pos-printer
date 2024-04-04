package id.modefashion.printer.worker;

import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import id.modefashion.printer.dto.ReceiptLineData;
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
      int totalLine = data.size();
      logger.info("totalLine: {}", totalLine);
      job.setPrintable(new PosReceipt(data, config));
      job.print();
    } catch (PrinterException e) {
      e.printStackTrace();
    }
  }
}
