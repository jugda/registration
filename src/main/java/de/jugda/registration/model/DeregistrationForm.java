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
public class DeregistrationForm {
    @NotBlank
    @FormParam("email")
    private String email;
    @FormParam("eventId")
    private String eventId;

    private Map<String, String> validationErrors = new HashMap<>();

    public void addValidationError(String key, String value) {
        validationErrors.put(key, value);
    }
}
