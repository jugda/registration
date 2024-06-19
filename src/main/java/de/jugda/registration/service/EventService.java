package de.jugda.registration.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.jugda.registration.Config;
import de.jugda.registration.model.Event;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@ApplicationScoped
public class EventService {

    @Inject
    ObjectMapper objectMapper;
    @Inject
    S3Client s3;
    @Inject
    Config config;

    private List<Event> allEvents;
    private Map<String, Map<String, String>> eventData;

    @SneakyThrows
    public List<Event> getAllEvents() {
        if (allEvents == null) {
            allEvents = objectMapper.readValue(new URL(config.events().jsonUrl()), new TypeReference<>() {});
        }
        return allEvents;
    }

    public Event getEvent(String eventId) {
        return getAllEvents().stream()
            .filter(event -> event.uid.startsWith(eventId.replaceAll("-", "")))
            .findFirst()
            .orElse(null);
    }

    @SneakyThrows
    public Map<String, Map<String, String>> getEventData() {
        if (eventData == null) {
            try (ResponseInputStream<GetObjectResponse> inputStream = s3.getObject(builder -> builder.bucket(config.events().dataBucket()).key(config.events().dataKey()))) {
                eventData = objectMapper.readValue(inputStream, new TypeReference<>() {});
            }
        }
        return eventData;
    }

    @SneakyThrows
    public void putEventData(String eventId, Map<String, String> data) {
        getEventData().put(eventId, data);
        byte[] bytes = objectMapper.writeValueAsBytes(eventData);
        s3.putObject(builder -> builder.bucket(config.events().dataBucket()).key(config.events().dataKey()),
            RequestBody.fromInputStream(new ByteArrayInputStream(bytes), bytes.length));
    }
}
