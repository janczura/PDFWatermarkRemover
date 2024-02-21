package io.github.janczura;


import io.github.janczura.watermarkremover.ColorWatermarkRemover;
import io.github.janczura.watermarkremover.RenMichWatermarkRemover;
import io.github.janczura.watermarkremover.WatermarkRemover;

import java.io.File;


public class Main {
    static String separator = File.separator;

    public static void main(String[] args) throws Exception {
        String pdfFilePath = "src" + separator + "main" + separator + "resources" + separator + "example.pdf"; // from https://renmich.faculty.wmi.amu.edu.pl/BSM/wyklady/Hasla.pdf
        String tempDirPath = PDFToImages.convertPdfToImages(pdfFilePath);
        //WatermarkRemover method = new PositionWatermarkRemover(147, 3053, 821, 3108);
        //WatermarkRemover method = new ColorWatermarkRemover(Color.BLACK);
        WatermarkRemover method = new RenMichWatermarkRemover(6);
        FileManager.removerWatermarks(method, tempDirPath);
        ImagesToPDF.convertImagesToPDF(tempDirPath, pdfFilePath + ".new.pdf");
        FileManager.removeTempDir(tempDirPath);
    }
}