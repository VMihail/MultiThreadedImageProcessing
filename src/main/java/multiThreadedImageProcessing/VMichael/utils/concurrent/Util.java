package multiThreadedImageProcessing.VMichael.utils.concurrent;

import java.util.ArrayList;
import java.util.List;

/**
 * Contain useful methods for dealing with multithreading
 */
public final class Util {
  /**
   * Divides the list into parts
   * @param list original list
   * @param partsCount number of parts
   * @return parts
   * @param <T> type of elements
   */
  public static <T> List<List<T>> divideIntoParts(final List<T> list, final int partsCount) {
    final List<List<T>> result = new ArrayList<>();
    final int wholePart = list.size() / partsCount;
    int remains = list.size() % partsCount;
    int left = 0;
    for (int i = 0; i < partsCount; i++) {
      int right = left + wholePart + (remains-- > 0 ? 1 : 0);
      result.add(list.subList(left, right));
      left = right;
    }
    return result;
  }

  /**
   * Normalizes the number of threads
   * @param have how many threads do we want
   * @param max how many threads we can get maximum
   * @return {@code min(have, max)}
   */
  public static int threadsCountNormalize(final int have, final int max) {
    return Math.min(have, max);
  }
}
