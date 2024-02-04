package read;

import java.io.IOException;
import java.util.Properties;

public interface PropsReader {
    Properties readProperties(String path) throws IOException;
}
