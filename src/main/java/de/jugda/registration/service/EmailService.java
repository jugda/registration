package de.jugda.registration.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import de.jugda.registration.BeanFactory;
import de.jugda.registration.model.Registration;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
public class EmailService {

    private final AmazonSimpleEmailService ses;

    public EmailService() {
        ses = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(System.getenv("SES_REGION")).build();
    }

    void sendRegistrationConfirmation(Registration registration) {
        String eventDate = isoToGermanDateFormat(registration.getEventId());

        String subject = "[JUG DA] Anmeldebestätigung für die Veranstaltung am " + eventDate;

        Map<String, Object> model = new HashMap<>();
        model.put("name", registration.getName());
        model.put("id", registration.getId());
        model.put("date", eventDate);

        HandlebarsService handlebarsService = BeanFactory.getHandlebarsService();
        String mailBody = handlebarsService.getRegistrationConfirmMail(model);

        SendEmailRequest request = new SendEmailRequest()
            .withSource("JUG Darmstadt <info@jug-da.de>")
            .withDestination(new Destination()
                .withToAddresses(registration.getEmail()))
            .withMessage(new Message()
                .withBody(new Body()
                    .withHtml(new Content()
                        .withCharset(StandardCharsets.UTF_8.name())
                        .withData(mailBody)))
                .withSubject(new Content()
                    .withCharset(StandardCharsets.UTF_8.name())
                    .withData(subject)));

        ses.sendEmail(request);
    }

    private String isoToGermanDateFormat(String iso) {
        String[] parts = iso.split("-");
        return parts[2] + "." + parts[1] + "." + parts[0];
    }
}
