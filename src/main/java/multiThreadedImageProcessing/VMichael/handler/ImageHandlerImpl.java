package multiThreadedImageProcessing.VMichael.handler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;

public class AbstractImageHandler implements ImageHandler {
  @Override
  public void convert(Path inputFile, int delta, Path outputFile, BiConsumer<int[], Integer> convertImpl) throws IOException {
    final var bytes = Files.readAllBytes(inputFile);
    final BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
    final int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
    convertImpl.accept(pixels, delta);
    final BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
    newImage.setRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
    final File output = new File(outputFile.toUri());
    ImageIO.write(newImage, FORMAT_NAME, output);
  }

  protected static void calc(int[] pixels, int i, int delta) {
    int red = (pixels[i] >> 16) & FF;
    int green = (pixels[i] >> 8) & FF;
    int blue = pixels[i] & FF;
    red = Math.min(Math.max((int) ((red - 128) * 1.5 + 128), 0), UNSIGNED_BYTE_MAX_VALUE);
    green = Math.min(Math.max((int) ((green - 128) * 1.5 + 128), 0), UNSIGNED_BYTE_MAX_VALUE);
    blue = Math.min(Math.max((int) ((blue - 128) * 1.5 + 128), 0), UNSIGNED_BYTE_MAX_VALUE);
    pixels[i] = (pixels[i] & 0xFF000000) | (red << 16) | (green << 8) | blue;
  }

  public static BiConsumer<int[], Integer> simpleImpl = (pixels, delta) -> {
    for (int i = 0; i < pixels.length; i++) {
      calc(pixels, i, delta);
    }
  };

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
}
