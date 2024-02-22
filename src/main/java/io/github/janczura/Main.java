package io.github.janczura;


import io.github.janczura.watermarkremover.ColorWatermarkRemover;
import io.github.janczura.watermarkremover.PositionWatermarkRemover;
import io.github.janczura.watermarkremover.RenMichWatermarkRemover;
import io.github.janczura.watermarkremover.WatermarkRemover;

import java.awt.*;
import java.io.File;
import java.util.Random;


public class Main {
    static String separator = File.separator;

    public static void main(String[] args) throws Exception {
        String pdfFilePath = "src" + separator + "main" + separator + "resources" + separator + "example.pdf"; // from https://renmich.faculty.wmi.amu.edu.pl/BSM/wyklady/Hasla.pdf
        String tempDirPath = PDFToImages.convertPdfToImages(pdfFilePath);
        WatermarkRemover method = switch (new Random().nextInt(3)) {
            case 0 -> new PositionWatermarkRemover(147, 3053, 821, 3108);
            case 1 -> new ColorWatermarkRemover(Color.BLACK);
            case 2 -> new RenMichWatermarkRemover(9);
            default -> null;
        };
        FileManager.removerWatermarks(method, tempDirPath);
        ImagesToPDF.convertImagesToPDF(tempDirPath, pdfFilePath + ".new.pdf");
        FileManager.removeTempDir(tempDirPath);
    }
}