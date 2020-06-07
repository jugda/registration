package de.jugda.registration.service;

import de.jugda.registration.model.Registration;
import io.quarkus.qute.Template;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@ApplicationScoped
public class EmailService {

    @Inject
    SesClient ses;
    @Inject
    Template mail_registration;
    @Inject
    Template mail_waitlist2attendee;

    void sendRegistrationConfirmation(Registration registration) {
        String eventDate = isoToGermanDateFormat(registration.getEventId());

        String subject = "[JUG DA] Anmeldebestätigung für die Veranstaltung am " + eventDate;

        String mailBody = mail_registration.data("name", registration.getName())
            .data("id", registration.getId())
            .data("date", eventDate)
            .data("waitlist", registration.isWaitlist())
            .render();

        sendEmail(registration, subject, mailBody);
    }

    void sendWaitlistToAttendeeConfirmation(Registration registration) {
        String eventDate = isoToGermanDateFormat(registration.getEventId());
        String subject = "[JUG DA] Dein Wartelisten-Eintrag für die Veranstaltung am " + eventDate;

        String mailBody = mail_waitlist2attendee.data("name", registration.getName())
            .data("id", registration.getId())
            .data("date", eventDate)
            .render();

        sendEmail(registration, subject, mailBody);
    }

    private void sendEmail(Registration registration, String subject, String mailBody) {
        ses.sendEmail(builder -> builder
            .source("JUG Darmstadt <info@jug-da.de>")
            .destination(db -> db.toAddresses(registration.getEmail()))
            .message(mb -> mb
                .subject(utf8Content(subject))
                .body(Body.builder()
                    .html(utf8Content(mailBody))
                    .text(utf8Content(strip(mailBody)))
                    .build()
                )
            )
        );
    }

    private Content utf8Content(String data) {
        return Content.builder()
            .charset(StandardCharsets.UTF_8.name())
            .data(data)
            .build();
    }

    private String isoToGermanDateFormat(String iso) {
        String[] parts = iso.split("-");
        return parts[2] + "." + parts[1] + "." + parts[0];
    }

    private static final Pattern TAGS = Pattern.compile("<.+?>");
    private String strip(String html) {
        return TAGS.matcher(html).replaceAll("").replaceAll(" {2}", "");
    }
}
