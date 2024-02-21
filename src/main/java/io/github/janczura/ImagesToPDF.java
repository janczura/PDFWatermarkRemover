package io.github.janczura;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ImagesToPDF {
    public static void convertImagesToPDF(String pathToImages, String outputPath) {
        try (PDDocument document = new PDDocument()) {
            File folder = new File(pathToImages);
            File[] files = folder.listFiles();

            int counter = 0;
            int maxSize = files.length;
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".png")) {
                    BufferedImage image = ImageIO.read(file);

                    PDPage page = new PDPage();
                    document.addPage(page);

                    page.setMediaBox(new PDRectangle(image.getWidth(), image.getHeight()));

                    PDPageContentStream contentStream = new PDPageContentStream(document, page);
                    contentStream.drawImage(PDImageXObject.createFromByteArray(document, toByteArray(image), null), 0, 0, image.getWidth(), image.getHeight());
                    contentStream.close();
                }
                if (counter % 5 == 0) {
                    System.out.println("Generating PDF: " + ((counter * 100) / maxSize) + "%");
                }
                counter = counter + 1;
            }
            document.save(outputPath);
            System.out.println("PDF generated.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    private static byte[] toByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
