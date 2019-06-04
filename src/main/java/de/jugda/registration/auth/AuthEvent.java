package de.jugda.registration.auth;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@Data
public class AuthEvent implements Serializable {
    private String type;
    private String methodArn;
    private String authorizationToken;
}
