package io.github.janczura;

import java.awt.image.BufferedImage;
import java.io.File;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;

public class PDFToImages {
    public static String convertPdfToImages(String pdfFilePath) throws Exception {
        String pdfFilePathWithoutFileName = removeFileNameFromPath(pdfFilePath);
        if (pdfFilePathWithoutFileName != null) {
            String tempFolderPath = createTempFolder(pdfFilePathWithoutFileName);
            try (PDDocument document = Loader.loadPDF(new File(pdfFilePath))) {
                PDFRenderer pdfRenderer = new PDFRenderer(document);

                int maxPages = document.getNumberOfPages();
                for (int page = 0; page < maxPages; ++page) {
                    BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                    String outputFilePath = tempFolderPath + File.separator + System.currentTimeMillis() + "page_" + (page + 1) + ".png";
                    ImageIO.write(bim, "png", new File(outputFilePath));

                    if (page % 5 == 0) {
                        System.out.println("Converting PDF file to images: " + ((int) (page * 100) / maxPages) + "%");
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to convert PDF to images");
            }
            return tempFolderPath;
        } else {
            return null;
        }
    }

    private static String createTempFolder(String path) throws Exception {
        String time = String.valueOf(System.currentTimeMillis());
        String folderPath = path + "temp" + time;
        File folder = new File(folderPath);
        if (!folder.exists()) {
            boolean created = folder.mkdir();
            if (created) {
                System.out.println("Temporary directory " + folderPath + " created");
            } else {
                System.out.println("Cannot create temporary directory " + folderPath);
                throw new Exception("Cannot create temporary directory " + folderPath);
            }
        } else {
            throw new Exception(folderPath + " exists.");
        }
        return folderPath;
    }

    private static String removeFileNameFromPath(String pdfFilePath) {
        int lastIndexOfSeparator = pdfFilePath.lastIndexOf(File.separator);
        if (lastIndexOfSeparator != -1) {
            return pdfFilePath.substring(0, lastIndexOfSeparator) + File.separator;
        } else {
            return null;
        }
    }
}
