package de.jugda.registration.model;

import io.quarkus.qute.TemplateExtension;

import java.util.Map;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
public class QuteExtension {

    @TemplateExtension
    static boolean containsKey(Map<?, ?> map, Object key) {
        return map.containsKey(key);
    }

}
