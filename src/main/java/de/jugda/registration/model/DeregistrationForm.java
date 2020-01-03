package de.jugda.registration.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.ws.rs.FormParam;

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
}
