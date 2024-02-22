package io.github.janczura.watermarkremover;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

import ij.ImagePlus;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.plugin.filter.RankFilters;

public class RenMichWatermarkRemover implements WatermarkRemover { //todo not perfect
    public final static int IMG_Y_SIZE = 3508;
    public final static int IMG_X_SIZE = 2479;
    private final int squareSizeToRemove;

    public RenMichWatermarkRemover(int squareSizeToRemove) {
        this.squareSizeToRemove = squareSizeToRemove;
    }

    @Override
    public void removeWatermark(File file) {
        int[][] blackPoints = new int[IMG_Y_SIZE][IMG_X_SIZE];
        try {
            BufferedImage image = ImageIO.read(file);
            loadImage(image, blackPoints);

            //todo set blackPoints[y][x] set value 2 to remove black color of "watermarks"
            //not perfect solution here
            for (int size = squareSizeToRemove; size > squareSizeToRemove - 4; size--) {
                findLargeBlackSquares(blackPoints, size);
            }
            remove(image, blackPoints);

            findIslands(10, blackPoints);
            remove(image, blackPoints);

            findBlackAfterLongWhiteSpace(blackPoints, 220, 150);
            remove(image, blackPoints);

            //image = fixImage(image); //makes it worse

            //end of solution
            ImageIO.write(image, "png", file);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private BufferedImage fixImage(BufferedImage image) {
        ImagePlus imagePlus = new ImagePlus("Example Image", image);
        ImageProcessor imageProcessor = imagePlus.getProcessor();
        RankFilters rankFilters = new RankFilters();
        rankFilters.rank(imageProcessor, 1, RankFilters.MEDIAN);

        ColorProcessor colorProcessor = (ColorProcessor) imageProcessor;
        float[] sharpenMatrix = {
                0, -1, 0,
                -1, 5, -1,
                0, -1, 0
        };
        colorProcessor.convolve(sharpenMatrix, 3, 3);
        return colorProcessor.getBufferedImage();
    }

    private void findBlackAfterLongWhiteSpace(int[][] blackPoints, int startFrom, int removeAfter) {
        for (int y = 0; y < blackPoints.length; y++) {
            int lastBlack = 0;
            boolean removeAllFromLine = false;
            for (int x = 0; x < blackPoints[0].length; x++) {
                if (removeAllFromLine || x < startFrom && blackPoints[y][x] == 1) {
                    blackPoints[y][x] = 2;
                } else if (x < startFrom || blackPoints[y][x] == 1) {
                    lastBlack = 0;
                } else {
                    lastBlack = lastBlack + 1;
                }
                if (!removeAllFromLine && x > startFrom && lastBlack >= removeAfter) {
                    removeAllFromLine = true;
                }
            }
        }
    }

    private void loadImage(BufferedImage image, int[][] blackPoints) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (Color.BLACK.equals(new Color(image.getRGB(x, y)))) {
                    blackPoints[y][x] = 1;
                } else {
                    blackPoints[y][x] = 0;
                }
            }
        }
    }

    private void remove(BufferedImage image, int[][] blackPoints) {
        for (int y = 0; y < blackPoints.length; y++) {
            for (int x = 0; x < blackPoints[0].length; x++) {
                if (blackPoints[y][x] == 2) {
                    image.setRGB(x, y, Color.WHITE.getRGB());
                    blackPoints[y][x] = 0;
                }
            }
        }
    }

    private void findIslands(int size, int[][] blackPoints) {
        int[][] islands = findIslands(blackPoints);
        for (int y = 0; y < blackPoints.length; y++) {
            for (int x = 0; x < blackPoints[0].length; x++) {
                if (islands[y][x] <= size) {
                    blackPoints[y][x] = 2;
                }
            }
        }
    }

    public static int[][] findIslands(int[][] blackPoints) {
        int height = blackPoints.length;
        int width = blackPoints[0].length;
        int[][] islands = new int[height][width];
        boolean[][] visited = new boolean[height][width];
        int islandIndex = 2;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (blackPoints[y][x] == 1 && !visited[y][x]) {
                    int size = bfs(blackPoints, visited, x, y, width, height, islandIndex);
                    markIsland(islands, visited, x, y, width, height, size, islandIndex);
                    islandIndex++;
                }
            }
        }
        return islands;
    }

    private static int bfs(int[][] blackPoints, boolean[][] visited, int startX, int startY, int width, int height, int islandIndex) {
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        int islandSize = 0;
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startX, startY});
        visited[startY][startX] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];
            islandSize++;

            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (newX >= 0 && newX < width && newY >= 0 && newY < height &&
                        blackPoints[newY][newX] == 1 && !visited[newY][newX]) {
                    queue.add(new int[]{newX, newY});
                    visited[newY][newX] = true;
                }
            }
        }
        return islandSize;
    }

    private static void markIsland(int[][] islands, boolean[][] visited, int startX, int startY, int width, int height, int size, int islandIndex) {
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startX, startY});
        visited[startY][startX] = false;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];
            islands[y][x] = size;

            int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (newX >= 0 && newX < width && newY >= 0 && newY < height &&
                        visited[newY][newX]) {
                    queue.add(new int[]{newX, newY});
                    visited[newY][newX] = false;
                }
            }
        }
    }

    public void findLargeBlackSquares(int[][] blackPoints, int squareSizeToRemove) {
        int height = blackPoints.length;
        int width = blackPoints[0].length;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (blackPoints[y][x] == 1) {
                    if (isBlackSquare(blackPoints, x, y, width, height, squareSizeToRemove)) {
                        markBlackSquare(blackPoints, x, y, squareSizeToRemove);
                    }
                }
            }
        }
    }

    public boolean isBlackSquare(int[][] blackPoints, int x, int y, int width, int height, int squareSizeToRemove) {
        if (blackPoints[y][x] != 1) {
            return false;
        }

        for (int i = 0; i < squareSizeToRemove; i++) {
            if (y + i >= height || x + i >= width || blackPoints[y + i][x] != 1 || blackPoints[y][x + i] != 1) {
                return false;
            }
        }

        return true;
    }

    public void markBlackSquare(int[][] blackPoints, int x, int y, int squareSizeToRemove) {
        for (int i = 0; i < squareSizeToRemove; i++) {
            for (int j = 0; j < squareSizeToRemove; j++) {
                blackPoints[y + i][x + j] = 2;
            }
        }
    }
}
