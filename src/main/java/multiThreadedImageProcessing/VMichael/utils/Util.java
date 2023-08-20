package multiThreadedImageProcessing.VMichael.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Useful Methods for Application Operation
 */
public final class Util {
  /**
   * Creates a list of integers from an array of integers
   * @param array int array
   * @return int list
   */
  public static List<Integer> intArrayToList(final int[] array) {
    final List<Integer> result = new ArrayList<>();
    Arrays.stream(array).forEach(result::add);
    return result;
  }

  /**
   * Creates an array of integers from a matrix of integers
   * @param matrix of integers
   * @return array of integers
   */
  public static int[] matrixToArray(final List<List<Integer>> matrix) {
    final AtomicInteger size = new AtomicInteger();
    matrix.forEach((line) -> size.addAndGet(line.size()));
    final int[] result = new int[size.get()];
    size.set(0);
    matrix.forEach((line) -> line.forEach((el) -> result[size.getAndIncrement()] = el));
    return result;
  }
}
