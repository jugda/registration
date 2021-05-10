package de.jugda.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.jugda.registration.model.Event;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@Slf4j
@SuppressWarnings({"rawtypes", "unchecked"})
public class LocalstackResource implements QuarkusTestResourceLifecycleManager {

    private static final int PORT = 4566;

    DockerComposeContainer localstack;

    @Override
    public Map<String, String> start() {
        createTestEvents();

        try {
            String localstackEndpoint = String.format("http://localhost:%s/health", PORT);
            if (((HttpURLConnection) new URL(localstackEndpoint).openConnection()).getResponseCode() == 200) {
                log.warn("Skip starting localstack testcontainer, as it seems there is already a running localstack on {} - no guarantees for succeeding tests!", localstackEndpoint);
                return null;
            }
        } catch (IOException e) {
            log.info("{} - Could not connect to running localstack, will start testcontainer instance.", e.getMessage());
        }


        localstack = new DockerComposeContainer(new File("docker-compose.yml"))
            .withLogConsumer("localstack", new Slf4jLogConsumer(log))
            .withExposedService("localstack", PORT,
                Wait.forLogMessage(".*\"Location\": \"/test\".*", 1));
        localstack.start();

        //noinspection HttpUrlsUsage
        String localstackUrl = String.format("http://%s:%s",
            localstack.getServiceHost("localhost", PORT), localstack.getServicePort("localstack", PORT));

        return Map.of("localstack-endpoint", localstackUrl);
    }

    @Override
    public void stop() {
        if (localstack != null) {
            localstack.stop();
        }
    }

    @SneakyThrows
    private void createTestEvents() {
        LocalDateTime ldt = LocalDateTime.now();
        Event event = new Event();
        event.setUid(ldt.format(DateTimeFormatter.BASIC_ISO_DATE) + "@jug-da.de");
        event.setSummary("Testtalk (John Doe)");
        event.setTitle("Testtalk");
        event.setSpeaker("John Doe");
        event.setLocation("Online");
        event.setUrl(String.format("https://www.jug-da.de/%s/%s/testtalk/", ldt.getYear(), ldt.format(DateTimeFormatter.ofPattern("MM"))));
        event.setStart(ldt);
        event.setEnd(ldt.plusHours(2));
        event.setTimezone("Europe/Berlin");
        List<Event> events = List.of(event);

        Writer w = new FileWriter("src/test/resources/s3/events.json");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.writeValue(w, events);
        w.close();
    }
}
