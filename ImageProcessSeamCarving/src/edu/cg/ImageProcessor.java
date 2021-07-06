package edu.cg;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class ImageProcessor extends FunctioalForEachLoops {

    // MARK: Fields
    public final int maximalColorValue = 255;
    public final String imageDimensionTooSmallMessageString = "Image dimension is too small for the following operation";
    public final Logger logger;
    public final BufferedImage workingImage;
    public final RGBWeights rgbWeights;
    public final int inWidth;
    public final int inHeight;
    public final int workingImageType;
    public final int outWidth;
    public final int outHeight;

    // MARK: Constructors
    public ImageProcessor(Logger logger, BufferedImage workingImage, RGBWeights rgbWeights, int outWidth, int outHeight) {
        super(); // Initializing for each loops...

        this.logger = logger;
        this.workingImage = workingImage;
        this.rgbWeights = rgbWeights;
        inWidth = workingImage.getWidth();
        inHeight = workingImage.getHeight();
        workingImageType = workingImage.getType();
        this.outWidth = outWidth;
        this.outHeight = outHeight;
        setForEachInputParameters();
    }

    public ImageProcessor(Logger logger, BufferedImage workingImage, RGBWeights rgbWeights) {
        this(logger, workingImage, rgbWeights, workingImage.getWidth(), workingImage.getHeight());
    }

    // MARK: Change picture hue - example
    public BufferedImage changeHue() {
        logger.log("Prepareing for hue changing...");

        int r = rgbWeights.redWeight;
        int g = rgbWeights.greenWeight;
        int b = rgbWeights.blueWeight;
        int max = rgbWeights.maxWeight;

        BufferedImage ans = newEmptyInputSizedImage();

        forEach((y, x) -> {
            Color c = new Color(workingImage.getRGB(x, y));
            int red = r * c.getRed() / max;
            int green = g * c.getGreen() / max;
            int blue = b * c.getBlue() / max;
            Color color = new Color(red, green, blue);
            ans.setRGB(x, y, color.getRGB());
        });

        logger.log("Changing hue done!");

        return ans;
    }

    // MARK: Nearest neighbor - example
    public BufferedImage nearestNeighbor() {
        logger.log("applies nearest neighbor interpolation.");
        BufferedImage ans = newEmptyOutputSizedImage();

        pushForEachParameters();
        setForEachOutputParameters();

        forEach((y, x) -> {
            int imgX = (int) Math.round((x * inWidth) / ((float) outWidth));
            int imgY = (int) Math.round((y * inHeight) / ((float) outHeight));
            imgX = Math.min(imgX, inWidth - 1);
            imgY = Math.min(imgY, inHeight - 1);
            ans.setRGB(x, y, workingImage.getRGB(imgX, imgY));
        });

        popForEachParameters();

        return ans;
    }

    // MARK: Unimplemented methods
    public BufferedImage greyscale() {
        logger.log("Prepareing for grayscale changing...");
        BufferedImage ans = newEmptyInputSizedImage();

        forEach((y, x) -> {
            Color c = new Color(workingImage.getRGB(x, y));
            int greyColorEntry = (c.getRed() * rgbWeights.redWeight + c.getGreen() * rgbWeights.greenWeight + c.getBlue() * rgbWeights.blueWeight) / (rgbWeights.redWeight + rgbWeights.greenWeight + rgbWeights.blueWeight);
            Color color = new Color(greyColorEntry, greyColorEntry, greyColorEntry);
            ans.setRGB(x, y, color.getRGB());
        });

        logger.log("Changing Image to grayscale done!");

        return ans;
    }

    public BufferedImage gradientMagnitude() {
        if (inWidth <= 1 || inHeight <= 1) {
            throw new IllegalArgumentException(imageDimensionTooSmallMessageString);
        }

        logger.log("Prepareing for gradient magnitude");
        BufferedImage greyscaleImage = greyscale();
        BufferedImage ans = newEmptyInputSizedImage();

        forEach((y, x) -> {
            int pixelColor = getGradientMagnitudePixelColor(x, y, greyscaleImage);
            Color currentPixelMagnitudeColor = new Color(pixelColor, pixelColor, pixelColor);
            ans.setRGB(x, y, currentPixelMagnitudeColor.getRGB());
        });

        logger.log("Changing Image to gradient magnitude done!");

        return ans;
    }

    private int getGradientMagnitudePixelColor(int x, int y, BufferedImage grayScaleImage) {
        int resultPixelColor;
        int[] currentGradient = new int[2];

        currentGradient[0] = getGradientXEntry(x, y, grayScaleImage);
        currentGradient[1] = getGradientYEntry(x, y, grayScaleImage);
        resultPixelColor = Math.min((int) (Math.sqrt(Math.pow(currentGradient[0], 2) + Math.pow(currentGradient[1], 2)) / 2), maximalColorValue);

        return resultPixelColor;
    }

    private int getGradientXEntry(int x1, int y, BufferedImage grayscaleImage) {
        int x2 = x1 + 1;

        if (x2 == grayscaleImage.getWidth()) {
            x2 = x1 - 1;
        }

        int x1PixelColor = (new Color(grayscaleImage.getRGB(x1, y)).getRed());
        int x2PixelColor = (new Color(grayscaleImage.getRGB(x2, y)).getRed());

        return x2PixelColor - x1PixelColor;
    }

    private int getGradientYEntry(int x, int y1, BufferedImage grayscaleImage) {
        int y2 = y1 + 1;
        if (y2 == grayscaleImage.getHeight()) {
            y2 = y1 - 1;
        }

        int y1PixelColor = (new Color(grayscaleImage.getRGB(x, y1)).getRed());
        int y2PixelColor = (new Color(grayscaleImage.getRGB(x, y2)).getRed());

        return y2PixelColor - y1PixelColor;
    }

    public BufferedImage bilinear() {
        logger.log("Prepareing for bilinear resize changing...");
        BufferedImage ans = newEmptyOutputSizedImage();
        pushForEachParameters();
        setForEachOutputParameters();

        forEach((y, x) -> {
            // relative location on new out picture
            float relativeX = (x * inWidth) / ((float) outWidth);
            float relativeY = (y * inHeight) / ((float) outHeight);

            int prevXPixel = (int) Math.floor(relativeX);
            int prevYPixel = (int) Math.floor(relativeY);

            int nextXPixel = Math.min(prevXPixel + 1, inWidth - 1);
            int nextYPixel = Math.min(prevYPixel + 1, inHeight - 1);

            float xDistance = getXDistanceBetweenPoints(prevXPixel, relativeX);
            float yDistance = getYDistanceBetweenPoints(prevYPixel, relativeY);
            Color c12 = getBilinearWeightedAveragingColor(new Color(workingImage.getRGB(prevXPixel, prevYPixel)), new Color(workingImage.getRGB(nextXPixel, prevYPixel)), xDistance);
            Color c34 = getBilinearWeightedAveragingColor(new Color(workingImage.getRGB(prevXPixel, nextYPixel)), new Color(workingImage.getRGB(nextXPixel, nextYPixel)), xDistance);
            Color resultColor = getBilinearWeightedAveragingColor(c12, c34, yDistance);
            ans.setRGB(x, y, resultColor.getRGB());
        });

        logger.log("Changing Image size by bilinear done!");

        return ans;
    }

    private float getXDistanceBetweenPoints(float xPoint1, float xPoint2) {
        return Math.abs(xPoint2 - xPoint1);
    }

    private float getYDistanceBetweenPoints(float yPoint1, float yPoint2) {
        return Math.abs(yPoint2 - yPoint1);
    }

    private Color getBilinearWeightedAveragingColor(Color c1, Color c2, float distance) {
        return new Color(getWeightedAveragingTint(c1.getRed(), c2.getRed(), distance), getWeightedAveragingTint(c1.getGreen(), c2.getGreen(), distance), getWeightedAveragingTint(c1.getBlue(), c2.getBlue(), distance));
    }

    private int getWeightedAveragingTint(int tint1, int tint2, float distance) {
        int averageTint = (int) Math.min(((1f - distance) * tint1 + distance * tint2), maximalColorValue);
        return Math.max(averageTint, 0);
    }

    // MARK: Utilities
    public final void setForEachInputParameters() {
        setForEachParameters(inWidth, inHeight);
    }

    public final void setForEachOutputParameters() {
        setForEachParameters(outWidth, outHeight);
    }

    public final BufferedImage newEmptyInputSizedImage() {
        return newEmptyImage(inWidth, inHeight);
    }

    public final BufferedImage newEmptyOutputSizedImage() {
        return newEmptyImage(outWidth, outHeight);
    }

    public final BufferedImage newEmptyImage(int width, int height) {
        return new BufferedImage(width, height, workingImageType);
    }

    public final BufferedImage duplicateWorkingImage() {
        BufferedImage output = newEmptyInputSizedImage();

        forEach((y, x) -> output.setRGB(x, y, workingImage.getRGB(x, y)));

        return output;
    }
}
