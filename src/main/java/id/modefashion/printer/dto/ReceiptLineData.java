package id.modefashion.printer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReceiptLineData {
  public static final String TYPE_TXT = "text";
  public static final String TYPE_IMG = "image";

  private String type;
  private String content;
}
