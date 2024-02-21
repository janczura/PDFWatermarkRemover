package io.github.janczura.watermarkremover;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ColorWatermarkRemover implements WatermarkRemover {
    private final Color colorToRemove;

    public ColorWatermarkRemover(Color color) {
        this.colorToRemove = color;
    }

    @Override
    public void removeWatermark(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    Color pixelColor = new Color(image.getRGB(x, y));

                    if (pixelColor.equals(colorToRemove)) {
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
