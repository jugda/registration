package de.jugda.registration.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import de.jugda.registration.model.Registration;
import io.quarkus.qute.Template;
import org.eclipse.microprofile.config.inject.ConfigProperty;

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
    Template mail_registration;
    @Inject
    Template mail_waitlist2attendee;

    @ConfigProperty(name = "aws.region.ses")
    String awsSesRegion;

    private final AmazonSimpleEmailService ses;

    public EmailService() {
        ses = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(System.getenv("SES_REGION")).build();
    }

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
        SendEmailRequest request = new SendEmailRequest()
            .withSource("JUG Darmstadt <info@jug-da.de>")
            .withDestination(new Destination()
                .withToAddresses(registration.getEmail()))
            .withMessage(new Message()
                .withSubject(utf8Content(subject))
                .withBody(new Body()
                    .withHtml(utf8Content(mailBody))
                    .withText(utf8Content(strip(mailBody)))
                )
            );

        ses.sendEmail(request);
    }

    private Content utf8Content(String data) {
        return new Content()
            .withCharset(StandardCharsets.UTF_8.name())
            .withData(data);
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
