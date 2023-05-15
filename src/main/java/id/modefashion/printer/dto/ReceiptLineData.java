package id.modefashion.printer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReceiptLineData {
  public static final String TYPE_TXT = "string";
  public static final String TYPE_IMG = "img/png";
  public static final String TYPE_BARCODE = "barcode";

  private String type;
  private String content;
}
