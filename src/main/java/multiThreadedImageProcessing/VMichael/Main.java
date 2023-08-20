package multiThreadedImageProcessing.VMichael;

import multiThreadedImageProcessing.VMichael.handler.ImageHandler;
import multiThreadedImageProcessing.VMichael.handler.ImageHandlerImpl;

import java.io.IOException;
import java.nio.file.Path;

import static multiThreadedImageProcessing.VMichael.handler.ImageHandlerImpl.simpleImpl;
import static multiThreadedImageProcessing.VMichael.handler.ImageHandlerImpl.twoThreadImpl;

/**
 * Increases the contrast of the picture
 */
public class Main {
  /**
   * program launch
   * @param args command line arguments: <input file name> <output file name> <delta> <threads count>
   */
  public static void main(final String ...args) {
    if (args.length != 4) {
      System.err.printf("Invalid number of arguments - %d, expected 4:" +
       " <input file name> <output file name> <delta> <threads count>", args.length);
    }
    try {
      final Path inputFile = Path.of(args[0]);
      final Path outputFile = Path.of(args[1]);
      final int delta = Integer.parseInt(args[2]);
      final int threads = Integer.parseInt(args[3]);
      final ImageHandler imageHandler = new ImageHandlerImpl();
      if (threads <= 0) {
        throw new IllegalArgumentException("natural number expected");
      }
      final long startTime = System.currentTimeMillis();
      if (threads == 1) {
        imageHandler.convert(inputFile, delta, outputFile, simpleImpl);
      } else if (threads == 2) {
        imageHandler.convert(inputFile, delta, outputFile, twoThreadImpl);
      } else {
        imageHandler.parallelConvert(inputFile, delta, outputFile, threads);
      }
      final long endTime = System.currentTimeMillis();
      System.out.printf("Image processed for %d", endTime - startTime);
    } catch (final NumberFormatException e) {
      System.err.println("Integer expected");
    } catch (final IOException e) {
      System.err.printf("I/O error: %s", e.getMessage());
    }
  }
}