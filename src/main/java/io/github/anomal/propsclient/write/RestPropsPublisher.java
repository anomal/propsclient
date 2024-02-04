package io.github.anomal.propsclient.write;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

public class RestPropsPublisher implements PropsPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestPropsPublisher.class);

    private final String url;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public RestPropsPublisher(String apiUrl) {
        this.url = apiUrl;
    }
    public boolean publish(String name, Properties props) {
        boolean isAccepted = false;
        try {
            JSONObject json = new JSONObject(props);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/" + name))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();
            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 202) {
                isAccepted = true;
            } else {
                LOGGER.error("Received HTTP status {} for {} {}", res.statusCode(), name, props);
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            return isAccepted;
        }
    }
}
