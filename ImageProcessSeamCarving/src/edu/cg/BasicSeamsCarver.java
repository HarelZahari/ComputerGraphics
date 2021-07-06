package edu.cg;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class BasicSeamsCarver extends ImageProcessor {

    private final double maximalColorValue = 255;
    private int[][] greyscaleImageMatrix;
    private double[][] seamCarvingCostMatrix;
    private Coordinate[][] offsetCoordinatesMatrix;
    private Seam[] verticalSeams;
    private Seam[] horizontalSeams;
    private CarvingDirection carveMode;
    private int amountOfVerticalSeams;
    private int amountOfHorizontalSeam;
    private int currentAmountOfVerticalSeams;
    private int currentAmountOfHorizontalSeams;
    private int currentWidth;
    private int currentHeight;

    // An enum describing the carving scheme used by the seams carver.
    // VERTICAL_HORIZONTAL means vertical seams are removed first.
    // HORIZONTAL_VERTICAL means horizontal seams are removed first.
    // INTERMITTENT means seams are removed intermittently : vertical, horizontal,
    // vertical, horizontal etc.
    public static enum CarvingScheme {
        VERTICAL_HORIZONTAL("Vertical seams first"), HORIZONTAL_VERTICAL("Horizontal seams first"), INTERMITTENT("Intermittent carving");

        public final String description;

        private CarvingScheme(String description) {
            this.description = description;
        }
    }

    public class Seam {
        private Coordinate[] seamCoordinates;
        private int indexForInsert;

        public Seam(int seamLength) {
            seamCoordinates = new Coordinate[seamLength];
            this.indexForInsert = 0;
        }

        public Coordinate[] getSeamCoordinates() {
            return seamCoordinates;
        }

        public void insertNewCoordinateToSeam(Coordinate coordinate) {
            if (indexForInsert < seamCoordinates.length) {
                seamCoordinates[indexForInsert] = coordinate;
                indexForInsert++;
            }
        }
    }

    public static enum CarvingDirection {
        VERTICAL, HORIZONTAL;
    }

    // A simple coordinate class which assists the implementation.
    protected class Coordinate {
        public int X;
        public int Y;

        public Coordinate(int X, int Y) {
            this.X = X;
            this.Y = Y;
        }
    }

    // TODO : Decide on the fields your BasicSeamsCarver should include. Refer to
    // the recitation and homework
    // instructions PDF to make an educated decision.
    public BasicSeamsCarver(Logger logger, BufferedImage workingImage, int outWidth, int outHeight, RGBWeights rgbWeights) {
        super((s) -> logger.log("Seam carving: " + s), workingImage, rgbWeights, outWidth, outHeight);
        this.currentHeight = inHeight;
        this.currentWidth = inWidth;
        this.amountOfHorizontalSeam = Math.abs(inHeight - outHeight);
        this.amountOfVerticalSeams = Math.abs(inWidth - outWidth);
        this.currentAmountOfVerticalSeams = 0;
        this.currentAmountOfHorizontalSeams = 0;
        this.verticalSeams = new Seam[amountOfVerticalSeams];
        this.horizontalSeams = new Seam[amountOfHorizontalSeam];
        BufferedImage greyscaleImage = greyscale();
        createGreyscaleImageMatrix(greyscaleImage);
        initOffsetCoordinatesMatrix();
    }

    private void createGreyscaleImageMatrix(BufferedImage greyscaleImage) {
        greyscaleImageMatrix = new int[currentHeight][currentWidth];

        for (int x = 0; x < greyscaleImage.getWidth(); x++) {
            for (int y = 0; y < greyscaleImage.getHeight(); y++) {
                greyscaleImageMatrix[y][x] = new Color(greyscaleImage.getRGB(x, y)).getRed();
            }
        }
    }

    private void initOffsetCoordinatesMatrix() {
        offsetCoordinatesMatrix = new Coordinate[currentHeight][currentWidth];

        for (int x = 0; x < currentWidth; x++) {
            for (int y = 0; y < currentHeight; y++) {
                offsetCoordinatesMatrix[y][x] = new Coordinate(x, y);
            }
        }
    }

    private void carveImage() {
        Seam currentSeam = findOptimalSeam();
        if (currentSeam != null) {
            removeOptimalSeam(currentSeam);
        }
    }

    private Seam findOptimalSeam() {
        Seam currentSeam = null;

        if (carveMode == CarvingDirection.HORIZONTAL) {
            createSeamCarvingHorizontalCostMatrix();
            logger.log("Carve the " + (currentAmountOfHorizontalSeams + 1) + " horizontal seam");
            currentSeam = getOptimalHorizontalSeam();
        }
        if (carveMode == CarvingDirection.VERTICAL) {
            createSeamCarvingVerticalCostMatrix();
            logger.log("Carve the " + (currentAmountOfVerticalSeams + 1) + " vertical seam");
            currentSeam = getOptimalVerticalSeam();
        }

        return currentSeam;
    }

    private Seam getOptimalVerticalSeam() {
        Seam currentVerticalSeam = new Seam(currentHeight);
        Coordinate currentCoordinate = getMinimalCoordinationOnLastRow(seamCarvingCostMatrix);

        while (currentCoordinate.Y >= 0) {
            currentVerticalSeam.insertNewCoordinateToSeam(currentCoordinate);
            currentCoordinate = findVerticalBacktrack(currentCoordinate);
        }

        return currentVerticalSeam;
    }

    private Coordinate findVerticalBacktrack(Coordinate currentCoordinate) {
        int x = currentCoordinate.X;
        int y = currentCoordinate.Y;

        if (isInBoundaries(x, y - 1) && seamCarvingCostMatrix[y][x] == pixelEnergy(x, y) + seamCarvingCostMatrix[y - 1][x] + getCVertical(x, y)) {
            return new Coordinate(x, y - 1);
        }
        if (isInBoundaries(x - 1, y - 1) && seamCarvingCostMatrix[y][x] == pixelEnergy(x, y) + seamCarvingCostMatrix[y - 1][x - 1] + getCLeft(x, y)) {
            return new Coordinate(x - 1, y - 1);
        } else {
            return new Coordinate(x + 1, y - 1);
        }
    }

    private Coordinate findHorizontalBacktrack(Coordinate currentCoordinate) {
        int x = currentCoordinate.X;
        int y = currentCoordinate.Y;

        if (isInBoundaries(x - 1, y - 1) && seamCarvingCostMatrix[y][x] == pixelEnergy(x, y) + seamCarvingCostMatrix[y - 1][x - 1] + getCUp(x, y)) {
            return new Coordinate(x - 1, y - 1);
        }
        if (isInBoundaries(x - 1, y) && seamCarvingCostMatrix[y][x] == pixelEnergy(x, y) + seamCarvingCostMatrix[y][x - 1] + getCHorizontal(x, y)) {
            return new Coordinate(x - 1, y);
        } else {
            return new Coordinate(x - 1, y + 1);
        }
    }

    private Seam getOptimalHorizontalSeam() {
        Seam currentHorizontalSeam = new Seam(currentWidth);
        Coordinate currentCoordinate = getMinimalCoordinationOnLastCol(seamCarvingCostMatrix);

        while (currentCoordinate.X >= 0) {
            currentHorizontalSeam.insertNewCoordinateToSeam(currentCoordinate);
            currentCoordinate = findHorizontalBacktrack(currentCoordinate);
        }

        return currentHorizontalSeam;
    }

    private Coordinate getMinimalCoordinationOnLastRow(double[][] seamCarvingCostMatrix) {
        int minimalXIndex = -1;
        double minimalValue = Double.MAX_VALUE;

        for (int x = 0; x < seamCarvingCostMatrix[currentHeight - 1].length; x++) {
            if (seamCarvingCostMatrix[currentHeight - 1][x] < minimalValue) {
                minimalValue = seamCarvingCostMatrix[currentHeight - 1][x];
                minimalXIndex = x;
            }
        }

        return new Coordinate(minimalXIndex, currentHeight - 1);
    }

    private Coordinate getMinimalCoordinationOnLastCol(double[][] seamCarvingCostMatrix) {
        int minimalYIndex = -1;
        double minimalValue = Double.MAX_VALUE;

        for (int y = 0; y < seamCarvingCostMatrix.length; y++) {
            if (seamCarvingCostMatrix[y][currentWidth - 1] < minimalValue) {
                minimalValue = seamCarvingCostMatrix[y][currentWidth - 1];
                minimalYIndex = y;
            }
        }

        return new Coordinate(currentWidth - 1, minimalYIndex);
    }

    private void removeOptimalSeam(Seam seam) {
        addOriginalCoordinatesSeam(seam);
        if (carveMode == CarvingDirection.VERTICAL) {
            updateMatricesVertical(seam);
        } else {
            updateMatricesHorizontal(seam);
        }
    }

    private void addOriginalCoordinatesSeam(Seam seam) {
        Seam originalCoordinatesSeam = new Seam(seam.seamCoordinates.length);

        for (int i = 0; i < originalCoordinatesSeam.seamCoordinates.length; i++) {
            // Get the original coordinate of the seam relative to the original picture.
            Coordinate currentCoordinate = offsetCoordinatesMatrix[seam.seamCoordinates[i].Y][seam.seamCoordinates[i].X];
            originalCoordinatesSeam.insertNewCoordinateToSeam(currentCoordinate);
        }

        if (carveMode == CarvingDirection.VERTICAL) {
            verticalSeams[currentAmountOfVerticalSeams] = originalCoordinatesSeam;
            currentAmountOfVerticalSeams++;
        } else {
            horizontalSeams[currentAmountOfHorizontalSeams] = originalCoordinatesSeam;
            currentAmountOfHorizontalSeams++;
        }
    }

    private void updateMatricesVertical(Seam seam) {
        currentWidth--;
        int[][] currentGreyscaleImageMatrix = new int[currentHeight][currentWidth];
        double[][] currentSeamCarvingCostMatrix = new double[currentHeight][currentWidth];
        Coordinate[][] currentOffsetCoordinatesMatrix = new Coordinate[currentHeight][currentWidth];
        Coordinate[] currentSeamCoordinates = seam.getSeamCoordinates();

        for (int y = currentHeight - 1; y >= 0; y--) {
            for (int x = 0; x < currentWidth + 1; x++) {
                if (currentSeamCoordinates[currentHeight - 1 - y].X > x) {
                    currentOffsetCoordinatesMatrix[y][x] = new Coordinate(offsetCoordinatesMatrix[y][x].X, offsetCoordinatesMatrix[y][x].Y);
                    currentSeamCarvingCostMatrix[y][x] = seamCarvingCostMatrix[y][x];
                    currentGreyscaleImageMatrix[y][x] = greyscaleImageMatrix[y][x];
                }
                if (currentSeamCoordinates[currentHeight - 1 - y].X < x) {
                    currentOffsetCoordinatesMatrix[y][x - 1] = new Coordinate(offsetCoordinatesMatrix[y][x].X, offsetCoordinatesMatrix[y][x].Y);
                    currentSeamCarvingCostMatrix[y][x - 1] = seamCarvingCostMatrix[y][x];
                    currentGreyscaleImageMatrix[y][x - 1] = greyscaleImageMatrix[y][x];
                }
            }
        }

        offsetCoordinatesMatrix = currentOffsetCoordinatesMatrix;
        seamCarvingCostMatrix = currentSeamCarvingCostMatrix;
        greyscaleImageMatrix = currentGreyscaleImageMatrix;
    }

    private void updateMatricesHorizontal(Seam seam) {
        currentHeight--;
        int[][] currentGreyscaleImageMatrix = new int[currentHeight][currentWidth];
        double[][] currentSeamCarvingCostMatrix = new double[currentHeight][currentWidth];
        Coordinate[][] currentOffsetCoordinatesMatrix = new Coordinate[currentHeight][currentWidth];
        Coordinate[] currentSeamCoordinates = seam.getSeamCoordinates();

        for (int x = currentWidth - 1; x >= 0; x--) {
            for (int y = 0; y < currentHeight + 1; y++) {
                if (currentSeamCoordinates[currentWidth - 1 - x].Y > y) {
                    currentOffsetCoordinatesMatrix[y][x] = new Coordinate(offsetCoordinatesMatrix[y][x].X, offsetCoordinatesMatrix[y][x].Y);
                    currentSeamCarvingCostMatrix[y][x] = seamCarvingCostMatrix[y][x];
                    currentGreyscaleImageMatrix[y][x] = greyscaleImageMatrix[y][x];
                }
                if (currentSeamCoordinates[currentWidth - 1 - x].Y < y) {
                    currentOffsetCoordinatesMatrix[y - 1][x] = new Coordinate(offsetCoordinatesMatrix[y][x].X, offsetCoordinatesMatrix[y][x].Y);
                    currentSeamCarvingCostMatrix[y - 1][x] = seamCarvingCostMatrix[y][x];
                    currentGreyscaleImageMatrix[y - 1][x] = greyscaleImageMatrix[y][x];
                }
            }
        }

        offsetCoordinatesMatrix = currentOffsetCoordinatesMatrix;
        seamCarvingCostMatrix = currentSeamCarvingCostMatrix;
        greyscaleImageMatrix = currentGreyscaleImageMatrix;
    }

    private void createSeamCarvingVerticalCostMatrix() {
        seamCarvingCostMatrix = new double[currentHeight][currentWidth];

        for (int y = 0; y < currentHeight; y++) {
            for (int x = 0; x < currentWidth; x++) {
                if (y == 0) {
                    seamCarvingCostMatrix[y][x] = pixelEnergy(x, y);
                } else {
                    seamCarvingCostMatrix[y][x] = getVerticalCostAtLocation(x, y);
                }
            }
        }
    }

    private void createSeamCarvingHorizontalCostMatrix() {
        seamCarvingCostMatrix = new double[currentHeight][currentWidth];

        for (int x = 0; x < currentWidth; x++) {
            for (int y = 0; y < currentHeight; y++) {
                if (x == 0) {
                    seamCarvingCostMatrix[y][x] = pixelEnergy(x, y);
                } else {
                    seamCarvingCostMatrix[y][x] = getHorizontalCostAtLocation(x, y);
                }
            }
        }
    }

    private double pixelEnergy(int x, int y) {
        return Math.sqrt(Math.pow(verticalEnergy(x, y), 2) + Math.pow(horizontalEnergy(x, y), 2));
    }

    private double horizontalEnergy(int x, int y) {
        double energyHorizontalResult;

        if (y < currentHeight - 1) {
            energyHorizontalResult = Math.abs(greyscaleImageMatrix[y][x] - greyscaleImageMatrix[y + 1][x]);
        } else {
            energyHorizontalResult = Math.abs(greyscaleImageMatrix[y][x] - greyscaleImageMatrix[y - 1][x]);
        }

        return energyHorizontalResult;
    }

    private double verticalEnergy(int x, int y) {
        double energyVerticalResult;

        if (x < currentWidth - 1) {
            energyVerticalResult = Math.abs(greyscaleImageMatrix[y][x] - greyscaleImageMatrix[y][x + 1]);
        } else {
            energyVerticalResult = Math.abs(greyscaleImageMatrix[y][x] - greyscaleImageMatrix[y][x - 1]);
        }

        return energyVerticalResult;
    }

    private double getVerticalCostAtLocation(int x, int y) {
        double cL = getCLeft(x, y);
        double cR = getCRight(x, y);
        double cV = getCVertical(x, y);

        return pixelEnergy(x, y) + getVerticalMinimalValue(x, y, cL, cR, cV);
    }

    private double getHorizontalCostAtLocation(int x, int y) {
        double cU = getCUp(x, y);
        double cD = getCDown(x, y);
        double cH = getCHorizontal(x, y);

        return pixelEnergy(x, y) + getHorizontalMinimalValue(x, y, cU, cD, cH);
    }

    private double getVerticalMinimalValue(int x, int y, double cL, double cR, double cV) {
        double right = Double.MAX_VALUE;
        double left = Double.MAX_VALUE;
        double vertical = Double.MAX_VALUE;

        if (isInBoundaries(x + 1, y - 1)) {
            right = seamCarvingCostMatrix[y - 1][x + 1] + cR;
        }
        if (isInBoundaries(x - 1, y - 1)) {
            left = seamCarvingCostMatrix[y - 1][x - 1] + cL;
        }
        if (isInBoundaries(x, y - 1)) {
            vertical = seamCarvingCostMatrix[y - 1][x] + cV;
        }

        return Math.min(Math.min(right, left), vertical);
    }

    private double getHorizontalMinimalValue(int x, int y, double cU, double cD, double cH) {
        double up = Double.MAX_VALUE;
        double down = Double.MAX_VALUE;
        double horizontal = Double.MAX_VALUE;

        if (isInBoundaries(x - 1, y - 1)) {
            up = seamCarvingCostMatrix[y - 1][x - 1] + cU;
        }
        if (isInBoundaries(x - 1, y + 1)) {
            down = seamCarvingCostMatrix[y + 1][x - 1] + cD;
        }
        if (isInBoundaries(x - 1, y)) {
            horizontal = seamCarvingCostMatrix[y][x - 1] + cH;
        }

        return Math.min(Math.min(up, down), horizontal);
    }

    private double getCLeft(int x, int y) {
        double cResult = maximalColorValue;

        if (isInBoundaries(x, y - 1) && isInBoundaries(x - 1, y)) {
            cResult = Math.abs(greyscaleImageMatrix[y - 1][x] - greyscaleImageMatrix[y][x - 1]) + Math.abs(getCVertical(x, y));
        }

        return cResult;
    }

    private double getCRight(int x, int y) {
        double cResult = maximalColorValue;

        if (isInBoundaries(x, y - 1) && isInBoundaries(x + 1, y)) {
            cResult = Math.abs(greyscaleImageMatrix[y - 1][x] - greyscaleImageMatrix[y][x + 1]) + Math.abs(getCVertical(x, y));
        }

        return cResult;
    }

    private double getCVertical(int x, int y) {
        double cResult = maximalColorValue;

        if (isInBoundaries(x - 1, y) && isInBoundaries(x + 1, y)) {
            cResult = Math.abs(greyscaleImageMatrix[y][x - 1] - greyscaleImageMatrix[y][x + 1]);
        }

        return cResult;
    }

    private double getCUp(int x, int y) {
        double cResult = maximalColorValue;

        if (isInBoundaries(x, y - 1) && isInBoundaries(x - 1, y)) {
            cResult = Math.abs(greyscaleImageMatrix[y - 1][x] - greyscaleImageMatrix[y][x - 1]) + Math.abs(getCHorizontal(x, y));
        }

        return cResult;
    }

    private double getCDown(int x, int y) {
        double cResult = maximalColorValue;

        if (isInBoundaries(x, y + 1) && isInBoundaries(x - 1, y)) {
            cResult = Math.abs(greyscaleImageMatrix[y + 1][x] - greyscaleImageMatrix[y][x - 1]) + Math.abs(getCHorizontal(x, y));
        }

        return cResult;
    }

    private double getCHorizontal(int x, int y) {
        double cResult = maximalColorValue;

        if (isInBoundaries(x, y - 1) && isInBoundaries(x, y + 1)) {
            cResult = Math.abs(greyscaleImageMatrix[y - 1][x] - greyscaleImageMatrix[y + 1][x]);
        }

        return cResult;
    }

    private boolean isInBoundaries(int x, int y) {
        return (x >= 0) && (x < currentWidth) && (y >= 0) && (y < currentHeight);
    }

    public BufferedImage carveImage(CarvingScheme carvingScheme) {
        if (carvingScheme == CarvingScheme.VERTICAL_HORIZONTAL) {
            carveAllVerticalSeams();
            carveAllHorizontalSeams();
        }

        if (carvingScheme == CarvingScheme.HORIZONTAL_VERTICAL) {
            carveAllHorizontalSeams();
            carveAllVerticalSeams();
        }

        if (carvingScheme == CarvingScheme.INTERMITTENT) {
            for (int i = 0; i < amountOfHorizontalSeam + amountOfVerticalSeams; i++) {
                if (i % 2 == 0 && currentAmountOfVerticalSeams < amountOfVerticalSeams) {
                    carveMode = CarvingDirection.VERTICAL;
                    carveImage();
                } else {
                    if (currentAmountOfHorizontalSeams < amountOfHorizontalSeam) {
                        carveMode = CarvingDirection.HORIZONTAL;
                        carveImage();
                    } else {
                        carveMode = CarvingDirection.VERTICAL;
                        carveImage();
                    }
                }
            }
        }

        logger.log("Changing Image size by seam carving done!");

        return createResizeImage();
    }

    private void carveAllVerticalSeams() {
        carveMode = CarvingDirection.VERTICAL;
        while (currentAmountOfVerticalSeams < amountOfVerticalSeams) {
            carveImage();
        }
    }

    private void carveAllHorizontalSeams() {
        carveMode = CarvingDirection.HORIZONTAL;
        while (currentAmountOfHorizontalSeams < amountOfHorizontalSeam) {
            carveImage();
        }
    }

    private BufferedImage createResizeImage() {
        BufferedImage ans = newEmptyOutputSizedImage();

        for (int x = 0; x < currentWidth; x++) {
            for (int y = 0; y < currentHeight; y++) {
                ans.setRGB(x, y, workingImage.getRGB(offsetCoordinatesMatrix[y][x].X, offsetCoordinatesMatrix[y][x].Y));
            }
        }

        return ans;
    }

    public BufferedImage showSeams(boolean showVerticalSeams, int seamColorRGB) {
        BufferedImage ans = newEmptyImage(inWidth, inHeight);

        if (showVerticalSeams) {
            carveAllVerticalSeams();
        } else {
            carveAllHorizontalSeams();
        }

        boolean[][] showSeamMatrix = createSeamShowMatrix(showVerticalSeams);

        for (int x = 0; x < inWidth; x++) {
            for (int y = 0; y < inHeight; y++) {
                if (showSeamMatrix[y][x]) {
                    ans.setRGB(x, y, seamColorRGB);
                } else {
                    ans.setRGB(x, y, workingImage.getRGB(x, y));
                }
            }
        }

        return ans;
    }

    private boolean[][] createSeamShowMatrix(boolean showVerticalSeams) {
        boolean[][] showSeamMatrix = new boolean[inHeight][inWidth];

        if (showVerticalSeams) {
            for (Seam currentSeam : verticalSeams) {
                for (Coordinate currentCoordinate : currentSeam.seamCoordinates) {
                    showSeamMatrix[currentCoordinate.Y][currentCoordinate.X] = true;
                }
            }
        } else {
            for (Seam currentSeam : horizontalSeams) {
                for (Coordinate currentCoordinate : currentSeam.seamCoordinates) {
                    showSeamMatrix[currentCoordinate.Y][currentCoordinate.X] = true;
                }
            }
        }

        return showSeamMatrix;
    }

}
