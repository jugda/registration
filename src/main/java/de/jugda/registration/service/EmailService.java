package de.jugda.registration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.jugda.registration.Config;
import de.jugda.registration.model.Event;
import de.jugda.registration.model.Registration;
import io.quarkus.qute.Template;
import io.quarkus.qute.api.ResourcePath;
import lombok.SneakyThrows;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.BulkEmailDestination;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.TemplateDoesNotExistException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@ApplicationScoped
public class EmailService {

    @Inject
    SesClient ses;
    @Inject
    Config config;
    @Inject
    EventService eventService;
    @Inject
    ObjectMapper objectMapper;
    @ResourcePath("mail/registration")
    Template tplRegistration;
    @ResourcePath("mail/waitlist2attendee")
    Template tplWaitlist2attendee;

    void sendRegistrationConfirmation(Registration registration) {
        Event event = eventService.getEvent(registration.eventId);
        String subject = String.format("[%s] Anmeldebestätigung für \"%s\" am %s",
            config.email.subjectPrefix, event.summary, event.startDate());

        String mailBody = tplRegistration
            .data("tenant", config.tenant)
            .data("registration", registration)
            .data("event", event)
            .render();

        sendEmail(registration, subject, mailBody);
    }

    void sendWaitlistToAttendeeConfirmation(Registration registration) {
        Event event = eventService.getEvent(registration.eventId);
        String subject = String.format("[%s] Dein Wartelisten-Eintrag für \"%s\" am %s",
            config.email.subjectPrefix, event.summary, event.startDate());

        String mailBody = tplWaitlist2attendee
            .data("tenant", config.tenant)
            .data("registration", registration)
            .data("event", event)
            .render();

        sendEmail(registration, subject, mailBody);
    }

    private void sendEmail(Registration registration, String subject, String mailBody) {
        ses.sendEmail(builder -> builder
            .source(config.email.from)
            .destination(db -> db.toAddresses(registration.getEmail()))
            .message(mb -> mb
                .subject(utf8Content(subject))
                .body(bb -> bb
                    .html(utf8Content(mailBody))
                    .text(utf8Content(strip(mailBody)))
                )
            )
        );
    }

    public void sendBulkEmail(List<Registration> registrations, String templateName, String subject, String body) {
        String tenantTemplateName = config.tenant.id + "_" + templateName;
        updateSesTemplate(tenantTemplateName, subject, body);

        String defaultTemplateData = objectToString(Map.of("tenant", config.tenant));

        List<BulkEmailDestination> destinations = registrations.stream()
            .map(registration -> BulkEmailDestination.builder()
                .destination(db -> db.toAddresses(registration.getEmail()))
                .replacementTemplateData(objectToString(registration))
                .build())
            .collect(Collectors.toList());

        ses.sendBulkTemplatedEmail(builder -> builder
            .template(tenantTemplateName)
            .defaultTemplateData(defaultTemplateData)
            .source(config.email.from)
            .destinations(destinations)
            .configurationSetName("BasicConfigSet")
        );
    }

    private void updateSesTemplate(String templateName, String subject, String body) {
        software.amazon.awssdk.services.ses.model.Template sesTemplate =
            software.amazon.awssdk.services.ses.model.Template.builder()
                .templateName(templateName)
                .subjectPart(subject)
                .textPart(body)
                .build();
        try {
            ses.updateTemplate(builder -> builder.template(sesTemplate));
        } catch (TemplateDoesNotExistException e) {
            ses.createTemplate(builder -> builder.template(sesTemplate));
        }
    }

    @SneakyThrows
    private String objectToString(Object o) {
        return objectMapper.writeValueAsString(o);
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
