package de.jugda.registration.model;

import io.quarkus.qute.TemplateData;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.ws.rs.FormParam;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@Data
@TemplateData
public class RegistrationForm {
    @FormParam("id")
    public String id;
    @FormParam("eventId")
    public String eventId;
    @NotBlank
    @FormParam("name")
    public String name;
    @NotBlank
    @FormParam("email")
    public String email;
    @FormParam("privacy")
    public String privacy;
    @FormParam("videoRecording")
    public String videoRecording;
    @FormParam("pub")
    public String pub;
    @FormParam("waitlist")
    public boolean waitlist;

    @FormParam("limit")
    public int limit;
    @FormParam("freeSeats")
    public int freeSeats;
    @FormParam("actualCount")
    public int actualCount;
    @FormParam("showPub")
    public boolean showPub;

    public Map<String, String> validationErrors = new HashMap<>();

    public void addValidationError(String key, String value) {
        validationErrors.put(key, value);
    }
}
