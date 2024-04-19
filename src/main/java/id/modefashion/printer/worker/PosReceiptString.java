package id.modefashion.printer.worker;

import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import javax.print.PrintService;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.Style.FontName;
import com.github.anastaciocintra.output.PrinterOutputStream;

public class PosReceiptString implements Printable {
  private static final Logger logger = LoggerFactory.getLogger(PosReceiptString.class);
  private String data;
  PropertiesConfiguration config;

  public PosReceiptString(String data, PropertiesConfiguration config) {
    this.data = data;
    this.config = config;
  }

  @Override
  public int print(java.awt.Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
    logger.info("PRINT CALL");
    if (pageIndex > 0) {
      return NO_SUCH_PAGE;
    }

    PrintService printService = PrinterOutputStream.getPrintServiceByName(config.getString("printer.name"));

    try (EscPos escpos = new EscPos(new PrinterOutputStream(printService))) {

      String font = config.getString("font.family");
      String fontSize = config.getString("font.size");
      Style title = new Style().setFontSize(Style.FontSize.valueOf(fontSize),
          Style.FontSize.valueOf(fontSize)).setFontName(FontName.valueOf(font));

      // Memecah data menjadi array berdasarkan karakter #
      String[] dataArray = this.data.split("#");

      for (String line : dataArray) {
        logger.info("LINE: {}", line);
        escpos.writeLF(title, line);
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
