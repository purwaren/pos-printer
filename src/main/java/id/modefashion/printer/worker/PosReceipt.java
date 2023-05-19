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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

import id.modefashion.printer.dto.ReceiptLineData;

public class PosReceipt implements Printable {
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
    logger.info("before print, x: {}, y: {}", x, y);
    logger.info("lineHeight from g2d: {}", lineHeight);
    int i = 0;
    for (ReceiptLineData line : this.data) {
      System.out.printf("%d: %s\n", i++, line.getContent());
      if (line.getType().equalsIgnoreCase(ReceiptLineData.TYPE_TXT)) {
        g2d.drawString(line.getContent(), x, y);
        y += lineHeight;
      } else if (line.getType().equalsIgnoreCase(ReceiptLineData.TYPE_IMG)) {
        // String[] base64str = line.getContent().split(",");
        // logger.info("array after split: {}", base64str.length);
        // byte[] img;
        // if (base64str.length > 1) {
        // logger.info("base64 contain meta-data image");
        // img = Base64.getDecoder().decode(base64str[1]);
        // } else {
        // img = Base64.getDecoder().decode(line.getContent());
        // }

        // try {
        // BufferedImage buff = ImageIO.read(new ByteArrayInputStream(img));
        // Image image = Toolkit.getDefaultToolkit().createImage(buff.getSource());
        // ImageObserver ob = new BarcodeObserver();
        // g2d.drawImage(image, x, y, (int) pf.getImageableWidth() - 10,
        // buff.getHeight(), ob);
        // y += buff.getHeight() + 10;
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        logger.info("base64 contain meta-data image but skipped");
      } else if (line.getType().equalsIgnoreCase(ReceiptLineData.TYPE_BARCODE)) {
        // do print with barcode libary zxing in here
        logger.info("contain meta-data barcode");
        Code128Writer barcodeWriter = new Code128Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(line.getContent(),
            BarcodeFormat.CODE_128, 400, 50);

        BufferedImage barcodeImage = new BufferedImage(bitMatrix.getWidth(),
            bitMatrix.getHeight(),
            BufferedImage.TYPE_INT_RGB);
        barcodeImage.createGraphics();

        for (int j = 0; j < bitMatrix.getWidth(); j++) {
          for (int k = 0; k < bitMatrix.getHeight(); k++) {
            barcodeImage.setRGB(j, k, bitMatrix.get(j, k) ? 0x000000 : 0xFFFFFF);
          }
        }
        g2d.drawImage(barcodeImage, x, y, (int) pf.getImageableWidth() - 15, 50,
            null);
        y += 70;
      } else {
        g2d.drawString(line.getContent(), x, y);
        y += lineHeight;
      }
    }

    logger.info("END PRINTER CALL");

    return PAGE_EXISTS;
  }
}
