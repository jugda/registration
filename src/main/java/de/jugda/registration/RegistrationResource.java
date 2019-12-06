package de.jugda.registration;

import de.jugda.registration.model.DeregistrationForm;
import de.jugda.registration.model.RegistrationForm;
import de.jugda.registration.service.DeleteService;
import de.jugda.registration.service.HandlebarsService;
import de.jugda.registration.service.RegistrationService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Path("registration")
@Produces(MediaType.TEXT_HTML)
public class RegistrationResource {

    @Inject
    RegistrationService registrationService;
    @Inject
    DeleteService deleteService;
    @Inject
    HandlebarsService handlebarsService;

    @GET
    public String getForm(@QueryParam("eventId") String eventId,
                          @QueryParam("limit") @DefaultValue("60") int limit,
                          @QueryParam("showPub") @DefaultValue("true") boolean showPub,
                          @QueryParam("deadline") String deadline) {
        if (deadline == null) {
            deadline = eventId + "T18:00:00+02:00";
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadlineTime = LocalDateTime.parse(deadline, DateTimeFormatter.ISO_DATE_TIME);
        LocalDate startDate = LocalDate.parse(eventId).minusMonths(1);

        String response = "";
        if (now.isAfter(deadlineTime)) {
            response = handlebarsService.getRegistrationClosed();
        } else if (now.toLocalDate().isBefore(startDate)) {
            Map<String, Object> model = new HashMap<>();
            model.put("eventId", eventId);
            model.put("startDate", startDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            response = handlebarsService.getRegistrationNotYetOpen(model);
        } else {
            int registrationCount = registrationService.getRegistrationCount(eventId);
            int freeSeats = limit - registrationCount;
            RegistrationForm form = new RegistrationForm();
            form.setEventId(eventId);
            form.setFreeSeats(freeSeats);
            form.setActualCount(registrationCount);
            form.setLimit(limit);
            form.setShowPub(showPub);
            form.setWaitlist(registrationCount >= limit);
            response = handlebarsService.getRegistrationForm(form);
        }

        return response;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String postForm(@Valid RegistrationForm registrationForm) {
        RegistrationForm registrationSaved = registrationService.handleRegistration(registrationForm);
        return handlebarsService.getThanksPage(registrationSaved);
    }

    @GET
    @Path("delete")
    public String getDeleteForm(@QueryParam("eventId") String eventId, @QueryParam("id") String id) {
        if (id == null) {
            Map<String, Object> model = Collections.singletonMap("eventId", eventId);
            return handlebarsService.getDeregistration(model);
        } else {
            String name = deleteService.deleteFromUri(id);
            return handlebarsService.getDeregistrationThanks(Collections.singletonMap("name", name));
        }
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String postDeleteForm(@Valid DeregistrationForm deregistrationForm) {
        String name = deleteService.deleteFromUi(deregistrationForm);
        return handlebarsService.getDeregistrationThanks(Collections.singletonMap("name", name));
    }

    @DELETE
    @Path("delete")
    public void deleteById(@QueryParam("id") String id) {
        deleteService.delete(id);
    }

}
