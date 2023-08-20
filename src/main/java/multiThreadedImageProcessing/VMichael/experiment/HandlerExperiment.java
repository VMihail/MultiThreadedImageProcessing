package multiThreadedImageProcessing.VMichael.experiment;

import multiThreadedImageProcessing.VMichael.handler.ImageHandler;

import java.io.IOException;
import java.nio.file.Path;

import static multiThreadedImageProcessing.VMichael.handler.ImageHandlerImpl.simpleImpl;
import static multiThreadedImageProcessing.VMichael.handler.ImageHandlerImpl.twoThreadImpl;

/**
 * Speed SpeedComparison
 * @param inputFile original file
 * @param delta how much do we increase the contrast
 * @param outputFile where do we save
 */
public record HandlerExperiment(Path inputFile, int delta, Path outputFile) {
  /**
   * Comparison of 1 and 2 threads
   * @param first impl
   * @param second impl
   * @throws IOException If the file is not found or cannot be read
   */
  public void oneAndTwoThreadStart(final ImageHandler first, final ImageHandler second) throws IOException {
    long startTime = System.currentTimeMillis();
    first.convert(inputFile, delta, outputFile, simpleImpl);
    long endTime = System.currentTimeMillis();
    System.out.printf("1 thread worked for %d\n", endTime - startTime);
    startTime = System.currentTimeMillis();
    second.convert(inputFile, delta, outputFile, twoThreadImpl);
    endTime = System.currentTimeMillis();
    System.out.printf("2 threads worked for %d", endTime - startTime);
  }

  /**
   * Comparison of an arbitrary number of threads
   * @param first impl
   * @param second impl
   * @param threads count
   * @throws IOException If the file is not found or cannot be read
   */
  public void start(final ImageHandler first, final ImageHandler second, final int threads) throws IOException {
    long startTime = System.currentTimeMillis();
    first.convert(inputFile, delta, outputFile, simpleImpl);
    long endTime = System.currentTimeMillis();
    System.out.printf("1 thread worked for %d\n", endTime - startTime);
    startTime = System.currentTimeMillis();
    second.parallelConvert(inputFile, delta, outputFile, threads);
    endTime = System.currentTimeMillis();
    System.out.printf("%d threads worked for %d", threads, endTime - startTime);
  }
}
