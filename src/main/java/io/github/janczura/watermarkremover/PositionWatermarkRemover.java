package io.github.janczura.watermarkremover;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class PositionWatermarkRemover implements WatermarkRemover {
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;

    public PositionWatermarkRemover(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void removeWatermark(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    if (x1 <= x && x2 >= x && y1 <= y && y2 >= y) {
                        image.setRGB(x, y, Color.WHITE.getRGB());
                    }
                }
            }
            ImageIO.write(image, "png", file);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
