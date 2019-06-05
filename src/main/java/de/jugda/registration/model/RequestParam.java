package de.jugda.registration.model;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.net.URLDecoder;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@UtilityClass
public class RequestParam {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";

    public static final Map<String, String> HEADER = Collections.singletonMap("Content-Type", "text/html; charset=utf-8");
    public static final String ENCODING = "UTF-8";

    public static final String ID = "id";
    public static final String EVENT_ID = "eventId";
    public static final String LIMIT = "limit";
    public static final String DEADLINE = "deadline";
    public static final String SHOW_PUB = "showPub";

    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PUB = "pub";
    public static final String WAITLIST = "waitlist";
    public static final String PRIVACY = "privacy";

    public static final String REGISTRATIONS = "registrations";

    @SneakyThrows
    public static Map<String, Object> parseBody(String body) {
        String decoded = URLDecoder.decode(body, RequestParam.ENCODING);

        return Arrays.stream(decoded.split("&"))
            .map(RequestParam::splitQueryParameter)
            .collect(Collectors.toMap(AbstractMap.SimpleImmutableEntry::getKey, AbstractMap.SimpleImmutableEntry::getValue));
    }

    private static AbstractMap.SimpleImmutableEntry<String, Object> splitQueryParameter(String it) {
        String[] parts = it.split("=");
        final String key = parts[0];
        final String value = parts.length > 1 ? parts[1].trim() : "";
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }
}
