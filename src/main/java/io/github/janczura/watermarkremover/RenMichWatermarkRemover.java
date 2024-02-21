package io.github.janczura.watermarkremover;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RenMichWatermarkRemover implements WatermarkRemover { //todo not perfect
    public final static int IMG_Y_SIZE = 3508;
    public final static int IMG_X_SIZE = 2479;
    private final int SQUARE_SIZE_TO_REMOVE;

    public RenMichWatermarkRemover(int squareSizeToRemove) {
        SQUARE_SIZE_TO_REMOVE = squareSizeToRemove;
    }

    @Override
    public void removeWatermark(File file) {
        int[][] blackPoints = new int[IMG_Y_SIZE][IMG_X_SIZE];
        try {
            BufferedImage image = ImageIO.read(file);
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    if (Circle.color.equals(new Color(image.getRGB(x, y)))) {
                        blackPoints[y][x] = 1;
                    }
                }
            }

            removeLargeBlackSquares(blackPoints); //not perfect solution here
            //todo set blackPoints[y][x] set value 2 to remove black color of "watermarks"

            for (int y = 0; y < IMG_Y_SIZE; y++) {
                for (int x = 0; x < IMG_X_SIZE; x++) {
                    if (blackPoints[y][x] == 2) {
                        image.setRGB(x, y, Color.WHITE.getRGB());
                    }
                }
            }
            ImageIO.write(image, "png", file);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void removeLargeBlackSquares(int[][] blackPoints) {
        int height = blackPoints.length;
        int width = blackPoints[0].length;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (blackPoints[y][x] == 1) {
                    if (isBlackSquare(blackPoints, x, y, width, height)) {
                        markBlackSquare(blackPoints, x, y, width, height);
                    }
                }
            }
        }
    }

    public boolean isBlackSquare(int[][] blackPoints, int x, int y, int width, int height) {
        if (blackPoints[y][x] != 1) {
            return false;
        }

        for (int i = 0; i < SQUARE_SIZE_TO_REMOVE; i++) {
            if (y + i >= height || x + i >= width || blackPoints[y + i][x] != 1 || blackPoints[y][x + i] != 1) {
                return false;
            }
        }

        return true;
    }

    public void markBlackSquare(int[][] blackPoints, int x, int y, int width, int height) {
        for (int i = 0; i < SQUARE_SIZE_TO_REMOVE; i++) {
            for (int j = 0; j < SQUARE_SIZE_TO_REMOVE; j++) {
                blackPoints[y + i][x + j] = 2;
            }
        }
    }


    private class Circle {
        //(x−a)2+(y−b)2=r2
        public static final int MINIMUM_RADIUS = 50;
        public static final int MAXIMUM_RADIUS = 177;
        public static final int POINTS_TO_CHECK = 20;
        public static final Color color = Color.BLACK;
        private List<Point> points = new ArrayList<>();
        private final int x;
        private final int y;
        private final int r;

        private Circle(int x, int y, int r) {
            this.x = x;
            this.y = y;
            this.r = r;
        }

        private boolean isCircleHere(int x, int y, BufferedImage image) {
            if (Circle.color.equals(new Color(image.getRGB(x, y)))) {
                int leftSide = ((x - getX()) * (x - getX())) + ((y - getY()) * (y - getY()));
                int rightSide = getR() * getR();
                return leftSide == rightSide;
            }
            return false;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getR() {
            return r;
        }

        private boolean isPointOnCircle(Point point) {
            int leftSide = ((point.getX() - getX()) * (point.getX() - getX())) + ((point.getY() - getY()) * (point.getY() - getY()));
            int rightSide = getR() * getR();
            return leftSide == rightSide;
        }

        public List<Point> getPoints() {
            return points;
        }
    }

    private class Point {
        private final int x;
        private final int y;

        private Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    class Line {
        public static final int MINIMUM_LENGTH = 65;
        public static final int MAXIMUM_LENGTH = 1030;
        // (y−yA)(xB−xA)−(yB−yA)(x−xA)=0
        int startX, startY, endX, endY;

        public Line(int startX, int startY, int endX, int endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }
    }

    private Point randomPoint(int xStart, int xEnd, int yStart, int yEnd) {
        Random random = new Random();
        int randomX = random.nextInt(xEnd - xStart + 1) + xStart;
        int randomY = random.nextInt(yEnd - yStart + 1) + yStart;
        Point point = new Point(randomX, randomY);
        return point;
    }
}
