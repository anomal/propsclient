package write;

import java.util.Properties;

public interface PropsPublisher {

    boolean publish(String name, Properties properties);

}
