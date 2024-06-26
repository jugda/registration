package de.jugda.registration;

import de.jugda.registration.model.RegistrationForm;
import de.jugda.registration.service.RegistrationService;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Path("registration")
@Produces(MediaType.TEXT_HTML)
public class RegistrationResource {

    @Inject
    RegistrationService registrationService;
    @Inject
    Validator validator;
    @Inject
    Config config;
    @Inject
    Template closed;
    @Inject
    Template not_yet_open;
    @Inject
    Template registration;
    @Inject
    Template thanks;

    @GET
    public TemplateInstance getForm(@QueryParam("eventId") String eventId,
                                    @QueryParam("limit") @DefaultValue("60") int limit,
                                    @QueryParam("showPub") @DefaultValue("false") boolean showPub,
                                    @QueryParam("hideVideoRecording") @DefaultValue("false") boolean hideVideoRecording,
                                    @QueryParam("hybrid") @DefaultValue("false") boolean hybrid,
                                    @QueryParam("deadline") String deadline,
                                    @QueryParam("opensBeforeInMonths") @DefaultValue("1") int opensBeforeInMonths) {
        if (deadline == null) {
            deadline = eventId + "T18:00:00+02:00";
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadlineTime = LocalDateTime.parse(deadline, DateTimeFormatter.ISO_DATE_TIME);
        LocalDate startDate = LocalDate.parse(eventId).minusMonths(opensBeforeInMonths);

        TemplateInstance response;
        if (now.isAfter(deadlineTime)) {
            response = closed.instance();
        } else if (now.toLocalDate().isBefore(startDate)) {
            response = not_yet_open.data("eventId", eventId)
                .data("startDate", startDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        } else {
            int registrationCount = registrationService.getRegistrationCount(eventId);
            int freeSeats = limit - registrationCount;
            RegistrationForm form = new RegistrationForm();
            form.setEventId(eventId);
            form.setFreeSeats(freeSeats);
            form.setActualCount(registrationCount);
            form.setLimit(limit);
            form.setShowPub(showPub);
            form.setHideVideoRecording(hideVideoRecording);
            form.setHybrid(hybrid);
            form.setWaitlist(registrationCount >= limit);
            response = registration.data("form", form).data("helptext", config.page().registration());
        }

        return response;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public TemplateInstance postForm(@BeanParam RegistrationForm registrationForm) {
        Set<ConstraintViolation<RegistrationForm>> violations = validator.validate(registrationForm);
        if (violations.isEmpty()) {
            RegistrationForm registrationSaved = registrationService.handleRegistration(registrationForm);
            return thanks.data("tenant", config.tenant()).data("reg", registrationSaved);
        } else {
            violations.forEach(cv ->
                registrationForm.addValidationError(cv.getPropertyPath().toString(), cv.getMessage()));
            return registration.data("form", registrationForm).data("helptext", config.page().registration());
        }
    }

}
