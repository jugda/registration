package de.jugda.registration.slack;

import javax.enterprise.context.ApplicationScoped;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@ApplicationScoped
public class SlackWebClient {

    private static final String SLACK_URL = "https://slack.com/api/";

    public void postMessage(String text, String channel) {
        String method = "chat.postMessage";

        Map<String, String> params = new HashMap<>();
        params.put("text", text);
        params.put("channel", channel);
        params.put("token", System.getenv("SLACK_OAUTH_ACCESS_TOKEN"));

        try {
            sendRequest(method, params);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void sendRequest(String method, Map<String, String> params) throws IOException {
        String postData = params.entrySet().stream()
            .map(e -> {
                try {
                    return e.getKey() + "=" + URLEncoder.encode(e.getValue(), "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    return e.getKey() + "=" + e.getValue();
                }
            })
            .collect(Collectors.joining("&"));

        URL url = new URL(SLACK_URL + method);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            byte [] postDataBytes = postData.getBytes(StandardCharsets.UTF_8);
            os.write(postDataBytes, 0, postDataBytes.length);
        }

        conn.getResponseCode();
    }

}
