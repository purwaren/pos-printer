package id.modefashion.printer.paper;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.print.Paper;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReceiptPaper extends Paper {
    private static final Logger logger = LoggerFactory.getLogger(ReceiptPaper.class); 

    public ReceiptPaper(PropertiesConfiguration config, int totalLine) {
        // calculate width & height, set paper size
        double width = config.getDouble("paper.width") * config.getInt("printer.dpi");
        double height = config.getDouble("paper.height") * config.getInt("printer.dpi");
        if (config.getInt("paper.height") <= 0) {
            Font font = new Font(config.getString("font.family"), Font.PLAIN, config.getInt("font.size"));
            FontMetrics metrics = new FontMetrics(font) {
            };
            logger.info("fontHeight: {}", metrics.getHeight());
            height = totalLine * config.getInt("font.line.height");
            height += config.getDouble("paper.margin.top") * config.getInt("printer.dpi")*2;
            height += config.getDouble("paper.margin.bottom") * config.getInt("printer.dpi")*2;
        }
        logger.info("paper width: {}, paper height: {}", width, height);
        setSize(width, height);

        //set imageable area
        double x = config.getDouble("paper.margin.left") * config.getInt("printer.dpi");
        double y = config.getDouble("paper.margin.top") * config.getInt("printer.dpi");
        double printWidth = (config.getDouble("paper.width") - config.getDouble("paper.margin.left")
                - config.getDouble("paper.margin.right")) * config.getInt("printer.dpi");
        double printHeight = height - (config.getDouble("paper.margin.top") + config.getDouble("paper.margin.bottom"))
                * config.getInt("printer.dpi");
        
        logger.info("Imageable Area x: {}, y: {}", x, y);
        logger.info("Imageable Area width: {}, height: {}", printWidth, printHeight);
        setImageableArea(x, y, printWidth, printHeight);
    }
}
