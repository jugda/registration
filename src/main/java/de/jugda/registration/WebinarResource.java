package de.jugda.registration;

import de.jugda.registration.model.Event;
import de.jugda.registration.service.EventService;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;
import io.quarkus.runtime.LaunchMode;

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
    LaunchMode launchMode;
    @Inject
    Config config;
    @ResourcePath("webinar/webinar")
    Template webinar;
    @ResourcePath("webinar/notAvailable")
    Template webinarNotAvailable;

    @GET
    @Path("{eventId}")
    public TemplateInstance getWebinar(@PathParam("eventId") String eventId) {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        if (!launchMode.isDevOrTest() && !eventId.equals(today)) {
            return webinarNotAvailable.data("tenant", config.tenant);
        }

        Event event = eventService.getEvent(eventId);
        Map<String, String> eventData = eventService.getEventData().get(eventId);
        eventData.put("webinarProvider", "zoom");

        return webinar.data("event", event)
            .data("tenant", config.tenant)
            .data("eventData", eventData)
            .data("helptext", config.page.webinar);
    }

}
