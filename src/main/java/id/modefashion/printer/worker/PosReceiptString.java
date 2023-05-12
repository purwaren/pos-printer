package id.modefashion.printer.worker;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.StringTokenizer;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PosReceiptString implements Printable {
  private static final Logger logger = LoggerFactory.getLogger(PosReceipt.class);
  private String data;
  PropertiesConfiguration config;

  public PosReceiptString(String data, PropertiesConfiguration config) {
    this.data = data;
    this.config = config;
  }

  @Override
  public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException, IllegalArgumentException {
    logger.info("PRINT CALL");

    if (pageIndex > 0) {
      return NO_SUCH_PAGE;
    }

    Graphics2D g2d = (Graphics2D) g;
    g2d.translate(pf.getImageableX(), pf.getImageableY());

    Font font = new Font(config.getString("font.family"), Font.PLAIN, config.getInt("font.size"));
    g2d.setFont(font);

    // int lineHeight = g2d.getFontMetrics().getHeight();
    float x = (float) pf.getImageableX();
    float y = (float) pf.getImageableY();
    double line_height = font.getSize() * 1.05D;
    StringTokenizer st = new StringTokenizer(this.data, "#");
    String line = null;
    while (st.hasMoreTokens()) {
      System.out.printf("%d: %s\n", 0, line);
      line = st.nextToken();
      if (line.length() > 0)
        g2d.drawString(line, x, y);
      else {
        g2d.drawString(" ", x, y);
      }
      y = (float) (y + line_height);
    }

    logger.info("END PRINTER CALL");

    return PAGE_EXISTS;
  }
}
