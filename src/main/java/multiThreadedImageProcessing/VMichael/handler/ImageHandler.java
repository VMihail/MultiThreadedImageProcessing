package multiThreadedImageProcessing.VMichael;

import java.io.IOException;
import java.nio.file.Path;

public interface ImageHandler {
  void convert(Path inputFile, int delta, Path outputFile) throws IOException;
}
