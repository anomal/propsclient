package io.github.anomal.propsclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import read.Monitor;
import read.RegexPropsReader;
import read.SimplePropsReader;
import read.PropsReader;
import write.PropsPublisher;
import write.RestPropsPublisher;

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

        PropsReader propertiesReader = new SimplePropsReader();
        Properties clientConf = null;
        try {
            clientConf = propertiesReader.readProperties(args[0]);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        String watchDir = clientConf.getProperty("inputdir");
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

        String apiUrl = clientConf.getProperty("apiurl");
        LOGGER.debug("apiurl is {}", apiUrl);
        String regex = clientConf.getProperty("regex");
        LOGGER.debug("regex is {}", regex);
        new Monitor(watchDirPath, regex, apiUrl).start();
    }
}