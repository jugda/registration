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
}
