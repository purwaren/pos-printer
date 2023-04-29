package id.modefashion.printer;

import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.AttributeSet;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import id.modefashion.printer.dto.ReceiptLineData;
import id.modefashion.printer.paper.ReceiptPaper;
import id.modefashion.printer.worker.PosReceipt;

/**
 * MPOS Java Printer
 * 
 * @author: purwaren
 */
public class App {

  public static void main(String[] args) {
    PrinterJob job = PrinterJob.getPrinterJob();

    try {
      PropertiesConfiguration config = new PropertiesConfiguration("printer.properties");
      job.setPrintService(findPrinterByName(config.getString("printer.name")));
      
      PageFormat pf = job.defaultPage();
      pf.setOrientation(config.getInt("paper.orientation"));
      pf.setPaper(new ReceiptPaper(config, 40));

      ReceiptLineData line1 = new ReceiptLineData("text", "MODE FASHION");
      ReceiptLineData line2 = new ReceiptLineData("text", "=======================");
      ReceiptLineData line3 = new ReceiptLineData("image",
          "iVBORw0KGgoAAAANSUhEUgAAAOAAAAB2CAIAAABecAjoAAABt0lEQVR42u3YS07DMBRA0WfE/rdsBpHcV/yRm7SiwDkjmhoHhSvHSYmk1hoRpZT286E/kuVvZyOP4/kssxnWM1+ZYf33/NS5zs2wf5XOnav/7+9fjZ3x6+rymI+ANyZQBAoCRaAgUBAoAgWBIlAQKAgUgYJAESgIFASKQEGgCBQECgJFoCBQBOoSIFAQKAIFgYJAESgIFIGCQEGgCBQEikBBoCBQBAoCRaAgUBAoAgWBIlAQKAgUgYJAQaAIFASKQEGgIFAECgJFoCBQECgCBYEiUBAoCBSBgkARKAgUBIpAQaAgUAQKAkWgIFAQKAIFgSJQECgIFIGCQBEoCBQEikBBoAgUBAoCRaAgUBAov0GptboKWEFBoPw5ny7B3Y6nlIho257j4yHvhfrjw5H5YPtqeHBxLoFyy6610hLJtfXf5o/9yFlq/cHZnLjFx7CqzcVvaH8kAn1Jwevl8DrLp1v8Y7vS09EMf/3inAL97y0OF7Nz+8JHF13tCnQrptke9NXd9E/09qA8f91FoO/ykHRlpOLd4p//kPTtLVL/Wn6xVZiFm+f0luru+tuM4xYPAkWgIFC4+QKHve3YUy9u2wAAAABJRU5ErkJggg==");
      List<ReceiptLineData> lineData = new ArrayList<>();
      lineData.add(line1);
      lineData.add(line2);
      lineData.add(line3);
      job.setPrintable(new PosReceipt(lineData, config), pf);
    } catch (PrinterException e) {
      e.printStackTrace();
    } catch (ConfigurationException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    }

    try {
      job.print();
    } catch (PrinterException ex) {
      ex.printStackTrace();
    }
  }

  private static PrintService findPrinterByName(String printerName) {
    PrintService[] printServices = findAllPrinters();

    for (PrintService printService : printServices) {
      if (printService.getName().trim().equals(printerName)) {
        return printService;
      }
    }

    return PrintServiceLookup.lookupDefaultPrintService();
  }

  private static PrintService[] findAllPrinters() {
    return PrintServiceLookup.lookupPrintServices((DocFlavor) null, (AttributeSet) null);
  }
}