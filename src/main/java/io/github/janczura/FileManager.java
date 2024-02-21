package io.github.janczura;

import io.github.janczura.watermarkremover.WatermarkRemover;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    public static void removerWatermarks(WatermarkRemover method, String tempDirPath) {
        List<File> images = FileManager.loadImagesFromPath(tempDirPath);
        int maxSize = images.size();
        int counter = 0;
        for (File image : images) {
            method.removeWatermark(image);
            if (counter % 5 == 0) {
                System.out.println("Removing watermarks: " + ((counter * 100) / maxSize) + "%");
            }
            counter = counter + 1;
        }
        System.out.println("Watermarks removed");
    }

    private static List<File> loadImagesFromPath(String path) {
        List<File> bufferedImages = new ArrayList<>();

        File folder = new File(path);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();

            for (File file : files) {
                if (isImageFile(file)) {
                    bufferedImages.add(file);
                }
            }
        } else {
            System.out.println("Path is not a directory.");
        }

        System.out.println("Loaded images: " + bufferedImages.size());
        return bufferedImages;
    }

    private static boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif");
    }

    public static void removeTempDir(String tempDirPath) {
        try {
            File tempDir = new File(tempDirPath);
            if (!tempDir.exists()) {
                System.out.println("Temporary directory does not exist.");
                return;
            }

            deleteDirectory(tempDir);
            System.out.println("Temporary directory removed successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
}
