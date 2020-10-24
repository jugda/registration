package de.jugda.registration;

import de.jugda.registration.model.Event;
import de.jugda.registration.model.Registration;
import de.jugda.registration.service.EmailService;
import de.jugda.registration.service.EventService;
import de.jugda.registration.service.ListService;
import io.quarkus.qute.Engine;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Path("admin/events")
@Produces(MediaType.TEXT_HTML)
public class AdminResource {

    @Inject
    ListService listService;
    @Inject
    EventService eventService;
    @Inject
    EmailService emailService;
    @Inject
    Template overview;
    @Inject
    Template list;
    @Inject
    Engine engine;

    @GET
    public TemplateInstance getAllEvents() {
        Map<String, Integer> events = listService.allEvents();

        return overview.data("events", events);
    }

    @GET
    @Path("{eventId}")
    public TemplateInstance getEventList(@PathParam("eventId") String eventId) {
        List<Registration> registrations = listService.singleEventRegistrations(eventId);
        Event event = eventService.getEvent(eventId);
        Map<String, String> eventData = eventService.getEventData().get(eventId);

        return list.data("eventId", eventId)
            .data("event", event)
            .data("eventData", eventData)
            .data("registrations", registrations);
    }

    @PUT
    @Path("{eventId}/data")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putEventData(@PathParam("eventId") String eventId, Map<String, String> data) {
        eventService.putEventData(eventId, data);
        return Response.noContent().build();
    }

    @PUT
    @Path("{eventId}/message")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendMessage(@PathParam("eventId") String eventId, Map<String, String> data) {
        List<Registration> registrations = listService.singleEventRegistrations(eventId);
        Template tplMessage = engine.parse(data.get("message"));

        registrations.forEach(person -> {
            String message = tplMessage.data("person", person).data("eventId", eventId).render();
            message = message.replaceAll("\\n", "<br>");
            emailService.sendEmail(person, data.get("subject"), message);
        });

        return Response.noContent().build();
    }

}
