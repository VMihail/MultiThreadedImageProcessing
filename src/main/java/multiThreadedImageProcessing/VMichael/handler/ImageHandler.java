package multiThreadedImageProcessing.VMichael.handler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiConsumer;

/**
 * Image Processor
 */
public interface ImageHandler {
  /** ubyte max value in 0x*/
  int FF = 0xff;

  /** ubyte max value in decimal */
  int UNSIGNED_BYTE_MAX_VALUE = 255;
  /** picture format */
  String FORMAT_NAME = "png";

  /**
   * converts the image
   * @param inputFile original file
   * @param delta how much do we increase the contrast
   * @param outputFile result file
   * @param convertImpl Implementation of the conversion
   * @throws IOException If the file is not found or cannot be read
   */
  void convert(Path inputFile, int delta, Path outputFile, BiConsumer<int[], Integer> convertImpl) throws IOException;

  /**
   * converts the image
   * @param inputFile original file
   * @param delta how much do we increase the contrast
   * @param outputFile result file
   * @param threads threads count
   * @throws IOException If the file is not found or cannot be read
   */
  void parallelConvert(Path inputFile, int delta, Path outputFile, int threads) throws IOException;
}