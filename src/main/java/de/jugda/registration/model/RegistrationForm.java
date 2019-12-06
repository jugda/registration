package de.jugda.registration.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@Data
public class RegistrationForm {
    private String id;
    private String eventId;
    @NotBlank
    private String name;
    @NotBlank
    private String email;
    private String privacy;
    private String pub;
    private boolean waitlist;

    private int limit;
    private int freeSeats;
    private int actualCount;
    private boolean showPub;
}
