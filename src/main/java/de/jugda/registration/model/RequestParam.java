package de.jugda.registration.model;

import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@UtilityClass
public class RequestParam {
    public static final Map<String, String> HEADER = Collections.singletonMap("Content-Type", "text/html");
    public static final String ENCODING = "ISO-8859-1";

    public static final String EVENT_ID = "eventId";
    public static final String LIMIT = "limit";
    public static final String DEADLINE = "deadline";

    public static final String NAME = "name";
    public static final String EMAIL = "email";

    public static final String REGISTRATIONS = "registrations";
}
