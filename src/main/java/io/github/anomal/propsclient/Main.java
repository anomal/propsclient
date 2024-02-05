package io.github.anomal.propsclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.anomal.propsclient.read.Monitor;
import io.github.anomal.propsclient.read.SimplePropsReader;
import io.github.anomal.propsclient.read.PropsReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Properties;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Path to config file missing");
            System.exit(1);
        }

        String configPath = args[0];

        PropsReader propertiesReader = new SimplePropsReader();
        Properties clientConf = null;
        try {
            clientConf = propertiesReader.readProperties(configPath);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        String watchDir = getProperty("inputdir", clientConf);
        LOGGER.debug("inputdir is {}", watchDir);
        Path watchDirPath = Paths.get(watchDir);
        if (!Files.exists(watchDirPath)) {
            System.err.println(String.format("Watch directory %s does not exist", watchDir));
            System.exit(1);
        }
        File watchDirFile = new File(watchDir);
        if (!watchDirFile.canRead() || !watchDirFile.canWrite()){
            System.err.println(String.format("Watch directory %s is not both readable and writable", watchDir));
            System.exit(1);
        }

        String apiUrl = getProperty("apiurl", clientConf);
        LOGGER.debug("apiurl is {}", apiUrl);
        String regex = getProperty("regex", clientConf);
        LOGGER.debug("regex is {}", regex);
        new Monitor(watchDirPath, regex, apiUrl).start();
    }

    private static String getProperty(String key, Properties properties) {
        if (!properties.containsKey(key)) {
            System.err.println(key + " missing from client configuration");
            System.exit(1);
        }
        return properties.getProperty(key);
    }
}