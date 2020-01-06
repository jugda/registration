package de.jugda.registration.slack;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Slf4j
@ApplicationScoped
public class SlackWebClient {

    private static final String SLACK_URL = "https://slack.com/api/";

    private static HttpClient httpClient = HttpClientBuilder.create().build();

    private final String oauthAccessToken;

    public SlackWebClient() {
        this.oauthAccessToken = System.getenv("SLACK_OAUTH_ACCESS_TOKEN");
    }

    public void postMessage(String text, String channel) {
        String method = "chat.postMessage";

        Map<String, String> reply = new HashMap<>();
        reply.put("text", text);
        reply.put("channel", channel);

        sendRequest(method, reply);
    }

    @SneakyThrows
    private void sendRequest(String method, Map<String, String> params) {
        log.info("Sending " + method + " request to Slack: " + params);

        params.put("token", oauthAccessToken);

        HttpPost post = new HttpPost(SLACK_URL + method);

        List<NameValuePair> nameValuePairs = params.entrySet().stream()
                .map(e -> new BasicNameValuePair(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
        post.setEntity(entity);

        httpClient.execute(post);
    }

}
