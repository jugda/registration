package de.jugda.registration;

import de.jugda.registration.model.Event;
import de.jugda.registration.service.EventService;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.runtime.LaunchMode;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

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
    LaunchMode launchMode;
    @Inject
    Config config;
    @Location("webinar/webinar")
    Template webinar;
    @Location("webinar/notAvailable")
    Template webinarNotAvailable;

    @GET
    @Path("{eventId}")
    public TemplateInstance getWebinar(@PathParam("eventId") String eventId) {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        if (!launchMode.isDevOrTest() && !eventId.equals(today)) {
            return webinarNotAvailable.data("tenant", config.tenant());
        }

        Event event = eventService.getEvent(eventId);
        Map<String, String> eventData = eventService.getEventData().get(eventId);

        return webinar.data("event", event)
            .data("tenant", config.tenant())
            .data("eventData", eventData)
            .data("helptext", config.page().webinar());
    }

}
