package id.modefashion.printer.worker;

import java.awt.Image;
import java.awt.image.ImageObserver;

public class BarcodeObserver implements ImageObserver {

  @Override
  public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
    return true;
  }
  
}
