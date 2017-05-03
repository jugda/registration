package de.jugda.registration.slack;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Log4j
@RequiredArgsConstructor
public class SlackWebClient {

    private static final String SLACK_URL = "https://slack.com/api/";

    private static HttpClient httpClient = HttpClientBuilder.create().build();

    private final String oauthAccessToken;

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

        HttpResponse response = httpClient.execute(post);
        parseResponseString(response);
    }

    @SneakyThrows
    private void parseResponseString(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        InputStream inputStream = response.getEntity().getContent();

        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer);
        String responseString = writer.toString();
        log.info("Slack responded with: " + statusCode + " - " + responseString);
    }

}
