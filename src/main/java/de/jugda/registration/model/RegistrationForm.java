package de.jugda.registration.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.ws.rs.FormParam;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@Data
public class RegistrationForm {
    @FormParam("id")
    private String id;
    @FormParam("eventId")
    private String eventId;
    @NotBlank
    @FormParam("name")
    private String name;
    @NotBlank
    @FormParam("email")
    private String email;
    @FormParam("privacy")
    private String privacy;
    @FormParam("pub")
    private String pub;
    @FormParam("waitlist")
    private boolean waitlist;

    @FormParam("limit")
    private int limit;
    @FormParam("freeSeats")
    private int freeSeats;
    @FormParam("actualCount")
    private int actualCount;
    @FormParam("showPub")
    private boolean showPub;

    private Map<String, String> validationErrors = new HashMap<>();

    public void addValidationError(String key, String value) {
        validationErrors.put(key, value);
    }
}
