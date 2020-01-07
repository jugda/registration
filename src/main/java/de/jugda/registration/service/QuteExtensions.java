package de.jugda.registration.service;

import de.jugda.registration.model.Registration;
import io.quarkus.qute.TemplateExtension;

import java.time.format.DateTimeFormatter;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
public class QuteExtensions {

    @TemplateExtension
    static String formattedCreationDate(Registration registration) {
        return registration.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

}
