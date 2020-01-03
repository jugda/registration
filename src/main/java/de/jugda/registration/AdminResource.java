package de.jugda.registration;

import de.jugda.registration.model.Registration;
import de.jugda.registration.service.ListService;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Path("list")
@Produces(MediaType.TEXT_HTML)
public class AdminResource {

    @Inject
    ListService listService;
    @Inject
    Template overview;
    @Inject
    Template list;

    @GET
    public TemplateInstance getAllEvents() {
        Map<String, Integer> events = listService.allEvents();

        return overview.data("events", events);
    }

    @GET
    @Path("{eventId}")
    public TemplateInstance getEventList(@PathParam("eventId") String eventId) {
        List<Registration> registrations = listService.singleEventRegistrations(eventId);

        return list.data("eventId", eventId)
            .data("registrations", registrations)
            .data("total", registrations.size());
    }

}
