package de.jugda.registration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {
    public String uid;
    public String summary;
    public String title;
    public String speaker;
    public String twitter;
    public String location;
    public String url;
    public LocalDateTime start;
    public LocalDateTime end;
    public String timezone;

    public String startDate() {
        return start.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public String startTime() {
        return start.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
