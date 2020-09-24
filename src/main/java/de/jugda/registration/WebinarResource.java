package de.jugda.registration;

import de.jugda.registration.model.Event;
import de.jugda.registration.service.EventService;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@Path("webinar")
@Produces(MediaType.TEXT_HTML)
public class WebinarResource {

    @Inject
    EventService eventService;
    @Inject
    Template webinar;
    @Inject
    Template webinarNotAvailable;

    @GET
    @Path("{eventId}")
    public TemplateInstance getWebinar(@PathParam("eventId") String eventId) {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        if (eventId.equals(today)) {
            return webinarNotAvailable.instance();
        }

        Event event = eventService.getEvent(eventId);
        Map<String, String> eventData = eventService.getEventData().get(eventId);
        eventData.put("webinarProvider", "zoom");

        return webinar.data("event", event)
            .data("eventData", eventData);
    }

}