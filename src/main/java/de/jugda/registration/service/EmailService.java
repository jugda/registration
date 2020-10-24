package de.jugda.registration.service;

import de.jugda.registration.model.Event;
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
    EventService eventService;
    @Inject
    Template mail_registration;
    @Inject
    Template mail_waitlist2attendee;

    void sendRegistrationConfirmation(Registration registration) {
        Event event = eventService.getEvent(registration.eventId);
        String subject = String.format("[JUG DA] Anmeldebestätigung für \"%s\" am %s", event.summary, event.startDate());

        String mailBody = mail_registration
            .data("registration", registration)
            .data("event", event)
            .render();

        sendEmail(registration, subject, mailBody);
    }

    void sendWaitlistToAttendeeConfirmation(Registration registration) {
        Event event = eventService.getEvent(registration.eventId);
        String subject = String.format("[JUG DA] Dein Wartelisten-Eintrag für \"%s\" am %s", event.summary, event.startDate());

        String mailBody = mail_waitlist2attendee
            .data("registration", registration)
            .data("event", event)
            .render();

        sendEmail(registration, subject, mailBody);
    }

    public void sendEmail(Registration registration, String subject, String mailBody) {
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

    private static final Pattern TAGS = Pattern.compile("<.+?>");
    private String strip(String html) {
        return TAGS.matcher(html).replaceAll("").replaceAll(" {2}", "");
    }
}
