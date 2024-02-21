package io.github.janczura.watermarkremover;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class GreyColorWatermarkRemover implements WatermarkRemover {
    private final int maxGreyValue;

    public GreyColorWatermarkRemover(int maxGreyValue) {
        this.maxGreyValue = maxGreyValue;
    }

    @Override
    public void removeWatermark(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    Color pixelColor = new Color(image.getRGB(x, y));

                    if (isPixelColorEqual(pixelColor) && pixelColor.getBlue() >= maxGreyValue) {
                        image.setRGB(x, y, Color.WHITE.getRGB());
                    }
                }
            }
            ImageIO.write(image, "png", file);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean isPixelColorEqual(Color pixelColor) {
        return pixelColor.getRed() == pixelColor.getGreen() && pixelColor.getGreen() == pixelColor.getBlue();
    }
}
