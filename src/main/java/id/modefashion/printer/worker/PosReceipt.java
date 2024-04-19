package id.modefashion.printer.worker;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.List;

import javax.print.PrintService;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.Style.FontName;
import com.github.anastaciocintra.escpos.barcode.BarCode;
import com.github.anastaciocintra.output.PrinterOutputStream;

import id.modefashion.printer.dto.ReceiptLineData;

public class PosReceipt implements Printable {
  private static final Logger logger = LoggerFactory.getLogger(PosReceipt.class);
  private List<ReceiptLineData> data;
  PropertiesConfiguration config;
  boolean printed = false;

  public PosReceipt(List<ReceiptLineData> data, PropertiesConfiguration config) {
    this.data = data;
    this.config = config;
  }

  @Override
  public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException, IllegalArgumentException {
    logger.info("PRINT CALL");
    if (printed) {
      return NO_SUCH_PAGE;
    } else {
      printed = true;
    }

    if (pageIndex > 0) {
      return NO_SUCH_PAGE;
    }

    PrintService printService = PrinterOutputStream.getPrintServiceByName(config.getString("printer.name"));

    try (EscPos escpos = new EscPos(new PrinterOutputStream(printService))) {

      String font = config.getString("font.family");
      String fontSize = config.getString("font.size");
      Style title = new Style()
          .setFontSize(Style.FontSize.valueOf(fontSize), Style.FontSize.valueOf(fontSize))
          .setFontName(FontName.valueOf(font));

      for (int i = 0; i < data.size() - 1; i++) {
        ReceiptLineData line = data.get(i);
        logger.info("line: {}", line.getContent());
        if (line.getType().equalsIgnoreCase(ReceiptLineData.TYPE_TXT)) {
          // do print with text
          escpos.writeLF(title, line.getContent());
        } else if (line.getType().equalsIgnoreCase(ReceiptLineData.TYPE_IMG)) {
          logger.info("base64 contain meta-data image but skipped");
        } else if (line.getType().equalsIgnoreCase(ReceiptLineData.TYPE_BARCODE)) {
          logger.info("contain meta-data barcode");
          BarCode barcode = new BarCode();
          // set barcode
          barcode.setHRIPosition(BarCode.BarCodeHRIPosition.BelowBarCode);
          barcode.setJustification(EscPosConst.Justification.Center);
          escpos.feed(1);
          escpos.write(barcode, line.getContent());
          barcode.setBarCodeSize(3, 100);
          escpos.feed(1);

        } else {
          logger.info("unknown type");
        }
      }

      escpos.feed(5);
      escpos.cut(EscPos.CutMode.FULL);
    } catch (Exception e) {
      e.printStackTrace();
    }

    logger.info("END PRINTER CALL");

    return PAGE_EXISTS;
  }
}
