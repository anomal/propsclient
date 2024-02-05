package io.github.anomal.propsclient.read;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.anomal.propsclient.write.PropsPublisher;
import io.github.anomal.propsclient.write.RestPropsPublisher;

public class Monitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Monitor.class);

    private final Path watchDirPath;
    private final PropsReader propsReader;
    private final PropsPublisher propsPublisher;

    public Monitor(Path watchDirPath, String regex, String apiUrl){
        this.watchDirPath = watchDirPath;
        this.propsReader = new RegexPropsReader(regex);
        this.propsPublisher = new RestPropsPublisher(apiUrl);
    }

    public void start() {
        WatchService watchService = null;
        try {
            watchService = FileSystems.getDefault().newWatchService();
            watchDirPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(1);
        }

        try {
            while (true) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        LOGGER.debug("{} was created", event.context());
                        Path pathFile = (Path) event.context();
                        String filename = pathFile.getFileName().toString();
                        if (filename.endsWith(".properties")) {
                            String name = filename.substring(0, filename.length() - ".properties".length());
                            try {
                                String filePath = watchDirPath.toAbsolutePath() + File.separator + filename;
                                Properties props = propsReader.readProperties(filePath);
                                LOGGER.debug("{} has {}", name, props);
                                boolean isAccepted = propsPublisher.publish(name, props);
                                if (isAccepted && !(new File(filePath).delete())) {
                                    LOGGER.error("Failed to delete {}", filePath);
                                }
                            } catch (IOException e) {
                                LOGGER.error(e.getMessage());
                            }
                        }
                    }
                }
                key.reset();
            }
        } catch (InterruptedException | RuntimeException e){
            LOGGER.error(e.getMessage(), e);
            System.exit(1);
        }
    }
}
