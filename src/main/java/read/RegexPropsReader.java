package read;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

public class RegexPropsReader implements PropsReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegexPropsReader.class);

    private final String regex;
    private final SimplePropsReader simplePropsReader = new SimplePropsReader();
    public RegexPropsReader(String regex) {
        this.regex = regex;
    }

    @Override
    public Properties readProperties(String path) throws IOException {
        Map<Object,Object> map = simplePropsReader.readProperties(path);
        for (Object key: map.keySet()) {
            String k = (String)key;
            LOGGER.debug("Examining key {}", k);
            if (!Pattern.matches(regex, k)) {
                LOGGER.debug("Removing {}", k);
                map.remove(k);
            }
        }
        return (Properties) map;
    }
}
