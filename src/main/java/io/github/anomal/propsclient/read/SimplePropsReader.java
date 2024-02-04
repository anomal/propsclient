package io.github.anomal.propsclient.read;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class SimplePropsReader implements PropsReader {

    public Properties readProperties(String path) throws IOException {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream(path)) {
            props.load(input);
        }
        return props;
    }

}
