package de.jugda.registration.model;

import io.quarkus.qute.TemplateData;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.FormParam;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@Data
@TemplateData
public class DeregistrationForm {
    @NotBlank
    @FormParam("email")
    public String email;
    @FormParam("eventId")
    public String eventId;

    public Map<String, String> validationErrors = new HashMap<>();

    public void addValidationError(String key, String value) {
        validationErrors.put(key, value);
    }
}
