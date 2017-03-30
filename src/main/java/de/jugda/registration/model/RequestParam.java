package de.jugda.registration.model;

import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@UtilityClass
public class RequestParam {
    public static final String GET = "GET";
    public static final String POST = "POST";

    public static final Map<String, String> HEADER = Collections.singletonMap("Content-Type", "text/html");
    public static final Map<String, String> HEADER_JSON = Collections.singletonMap("Content-Type", "application/json");
    public static final String ENCODING = "UTF-8";

    public static final String EVENT_ID = "eventId";
    public static final String LIMIT = "limit";
    public static final String DEADLINE = "deadline";

    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String TWITTER = "twitter";

    public static final String REGISTRATIONS = "registrations";
    public static final String TYPE = "type";
}
