package de.jugda.registration.service;

import de.jugda.registration.model.Registration;
import io.quarkus.qute.TemplateExtension;

import java.text.SimpleDateFormat;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
public class QuteExtensions {

    @TemplateExtension
    static String formattedCreationDate(Registration registration) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(registration.getCreated());
    }

}
