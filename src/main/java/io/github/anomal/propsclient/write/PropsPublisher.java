package io.github.anomal.propsclient.write;

import java.util.Properties;

public interface PropsPublisher {

    boolean publish(String name, Properties properties);

}
