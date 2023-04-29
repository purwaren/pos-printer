package id.modefashion.printer.worker;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import id.modefashion.printer.dto.ReceiptLineData;

public class PosReceipt implements Printable, ImageObserver {
  private static final Logger logger = LoggerFactory.getLogger(PosReceipt.class); 
  private List<ReceiptLineData> data;
  PropertiesConfiguration config;

  public PosReceipt(List<ReceiptLineData> data, PropertiesConfiguration config) {
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

    int lineHeight = g2d.getFontMetrics().getHeight();
    int x = (int) pf.getImageableX();
    int y = (int) pf.getImageableY();
    
    for (ReceiptLineData line : this.data) {
      if (line.getType() == ReceiptLineData.TYPE_TXT) {
        g2d.drawString(line.getContent(), x, y);
        y += lineHeight;
      } else if (line.getType() == ReceiptLineData.TYPE_IMG) {
        byte[] img = Base64.getDecoder().decode(line.getContent());
        try {
          BufferedImage buff = ImageIO.read(new ByteArrayInputStream(img));
          Image image = Toolkit.getDefaultToolkit().createImage(buff.getSource());
          ImageObserver ob = new BarcodeObserver();       
          g2d.drawImage(image, x, y, (int) pf.getImageableWidth()-10, (int) lineHeight*8, ob);
          y += buff.getHeight();
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        g2d.drawString(line.getContent(), x, y);
        y += lineHeight;
      }
    }

    logger.info("END PRINTER CALL");

    return PAGE_EXISTS;
  }

  @Override
  public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
    return true;
  }
}
