package multiThreadedImageProcessing.VMichael.handler;

import multiThreadedImageProcessing.VMichael.utils.Util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * impl of {@link ImageHandler}
 */
public class ImageHandlerImpl implements ImageHandler {
  /**
   * converts the image
   * @param inputFile original file
   * @param delta how much do we increase the contrast
   * @param outputFile result file
   * @param convertImpl Implementation of the conversion
   * @throws IOException If the file is not found or cannot be read
   */
  @Override
  public void convert(Path inputFile, int delta, Path outputFile, BiConsumer<int[], Integer> convertImpl) throws IOException {
    final BufferedImage image = getImage(inputFile);
    final int[] pixels = getPixels(image);
    convertImpl.accept(pixels, delta);
    save(image, pixels, outputFile);
  }

  /**
   * converts the image
   * @param inputFile original file
   * @param delta how much do we increase the contrast
   * @param outputFile result file
   * @param threads threads count
   * @throws IOException If the file is not found or cannot be read
   */
  @Override
  public void parallelConvert(Path inputFile, int delta, Path outputFile, int threads) throws IOException {
    final BufferedImage image = getImage(inputFile);
    int[] pixels = getPixels(image);
    threads = multiThreadedImageProcessing.VMichael.utils.concurrent.Util.threadsCountNormalize(threads, pixels.length);
    pixels = doParallel(pixels, delta, threads);
    save(image, pixels, outputFile);
  }

  /**
   * Converts to 1 thread
   */
  public static BiConsumer<int[], Integer> simpleImpl = (pixels, delta) -> {
    for (int i = 0; i < pixels.length; i++) {
      calc(pixels, i, delta);
    }
  };

  /**
   * Converts to 2 threads
   */
  public static BiConsumer<int[], Integer> twoThreadImpl = (pixels, delta) -> {
    final Thread first = new Thread(() -> {
      for (int i = 0; i < pixels.length >> 1; i++) {
        calc(pixels, i, delta);
      }
    });
    final Thread second = new Thread(() -> {
      for (int i = pixels.length >> 1; i < pixels.length; i++) {
        calc(pixels, i, delta);
      }
    });
    first.start();
    second.start();
    try {
      first.join();
      second.join();
    } catch (final InterruptedException e) {
      System.err.printf("Multi threads error: %s", e.getMessage());
    }
  };

  /**
   * Finds a new pixel value in array
   * @param pixels array of pixels
   * @param i index
   * @param delta how much do we increase the contrast
   */
  private static void calc(int[] pixels, int i, int delta) {
    int red = (pixels[i] >> 16) & FF;
    int green = (pixels[i] >> 8) & FF;
    int blue = pixels[i] & FF;
    red = Math.min(Math.max((int) ((red - delta) * 1.5 + delta), 0), UNSIGNED_BYTE_MAX_VALUE);
    green = Math.min(Math.max((int) ((green - delta) * 1.5 + delta), 0), UNSIGNED_BYTE_MAX_VALUE);
    blue = Math.min(Math.max((int) ((blue - delta) * 1.5 + delta), 0), UNSIGNED_BYTE_MAX_VALUE);
    pixels[i] = (pixels[i] & 0xFF000000) | (red << 16) | (green << 8) | blue;
  }

  /**
   * Finds a new pixel value in array in list
   * @param part list of pixels
   * @param j index
   * @param delta how much do we increase the contrast
   */
  private static void calc(final List<Integer> part, int j, int delta) {
    int red = (part.get(j) >> 16) & FF;
    int green = (part.get(j) >> 8) & FF;
    int blue = part.get(j) & FF;
    red = Math.min(Math.max((int) ((red - delta) * 1.5 + delta), 0), UNSIGNED_BYTE_MAX_VALUE);
    green = Math.min(Math.max((int) ((green - delta) * 1.5 + delta), 0), UNSIGNED_BYTE_MAX_VALUE);
    blue = Math.min(Math.max((int) ((blue - delta) * 1.5 + delta), 0), UNSIGNED_BYTE_MAX_VALUE);
    part.set(j, (part.get(j) & 0xFF000000) | (red << 16) | (green << 8) | blue);
  }

  /**
   * multithreaded contrast enhancement
   * @param pixels array of pixels
   * @param delta how much do we increase the contrast
   * @param threads count
   * @return new array of pixels
   */
  private int[] doParallel(int[] pixels, int delta, int threads) {
    final List<Integer> list = Util.intArrayToList(pixels);
    final List<List<Integer>> parts = multiThreadedImageProcessing.VMichael.utils.concurrent.Util.divideIntoParts(list, threads);
    final Thread[] threadArray = new Thread[threads];
    for (int i = 0; i < threads; i++) {
      final int index = i;
      final Thread thread = new Thread(() -> {
        final var part = parts.get(index);
        for (int j = 0; j < part.size(); j++) {
          calc(part, j, delta);
        }
      });
      threadArray[i] = thread;
      thread.start();
    }
    Arrays.stream(threadArray).forEach((thread -> {
      try {
        thread.join();
      } catch (final InterruptedException e) {
        throw new RuntimeException(e);
      }
    }));
    return Util.matrixToArray(parts);
  }

  /**
   * Get image
   * @param inputFile original image
   * @return BufferedImage of original image
   * @throws IOException if file not found or I/O error
   */
  private BufferedImage getImage(final Path inputFile) throws IOException {
    final var bytes = Files.readAllBytes(inputFile);
    return ImageIO.read(new ByteArrayInputStream(bytes));
  }

  /**
   * Get pixels array
   * @param image original image
   * @return pixels array
   */
  private int[] getPixels(final BufferedImage image) {
    return image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
  }

  /**
   * saves the image
   * @param image image to save
   * @param pixels array of pixels
   * @param outputFile output file
   * @throws IOException if file not found or I/O error
   */
  private void save(final BufferedImage image, final int[] pixels, final Path outputFile) throws IOException {
    final BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
    newImage.setRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
    final File output = new File(outputFile.toUri());
    ImageIO.write(newImage, FORMAT_NAME, output);
  }
}
