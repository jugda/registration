package de.jugda.registration;

import de.jugda.registration.model.Registration;
import de.jugda.registration.service.HandlebarsService;
import de.jugda.registration.service.ListService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
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
    HandlebarsService handlebarsService;

    @GET
    public String getAllEvents() {
        Map<String, Integer> events = listService.allEvents();

        Map<String, Object> model = new HashMap<>();
        model.put("events", events);
        return handlebarsService.getRegistrationsOverview(model);
    }

    @GET
    @Path("{eventId}")
    public String getEventList(@PathParam("eventId") String eventId) {
        List<Registration> registrations = listService.singleEventRegistrations(eventId);

        Map<String, Object> model = new HashMap<>();
        model.put("eventId", eventId);
        model.put("registrations", registrations);
        model.put("total", registrations.size());
        return handlebarsService.getRegistrationsList(model);
    }

}
