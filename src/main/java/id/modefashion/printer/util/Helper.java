package id.modefashion.printer.util;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.AttributeSet;

public class Helper {
  public static PrintService findPrinterByName(String printerName) {
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

  public static String[] getAllPrinterNames() {
    PrintService[] printServices = findAllPrinters();
    String[] names = new String[printServices.length];
    for (int i = 0; i < printServices.length; i++) {
      names[i] = printServices[i].getName();
    }
    return names;
  }
}
