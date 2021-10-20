package de.jugda.registration;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.io.TimezoneAssignment;
import biweekly.property.Classification;
import biweekly.property.Organizer;
import biweekly.property.Status;
import de.jugda.registration.model.Event;
import de.jugda.registration.service.EventService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@Path("ical")
public class CalendarResource {

    @Inject
    EventService eventService;
    @Inject
    Config config;

    @GET
    @Path("{eventId}")
    @Produces("text/calendar")
    public Response getICalEntry(@PathParam("eventId") String eventId) {
        Event event = eventService.getEvent(eventId);

        ICalendar ical = new ICalendar();
        TimezoneAssignment tz = TimezoneAssignment.download(TimeZone.getTimeZone(event.getTimezone()), false);
        ical.getTimezoneInfo().setDefaultTimezone(tz);

        VEvent vEvent = new VEvent();
        vEvent.setUid(event.getUid());
        vEvent.setDateTimeStamp(new Date());
        vEvent.setDateStart(Date.from(event.getStart().atZone(ZoneId.of(event.getTimezone())).toInstant()));
        vEvent.setDateEnd(Date.from(event.getEnd().atZone(ZoneId.of(event.getTimezone())).toInstant()));
        vEvent.setSummary(config.email().subjectPrefix() + ": " + event.getSummary());
        vEvent.setDescription(String.format("%s\n\n%s\n\nAlle weiteren Infos: %s", config.tenant().name(), event.getSummary(), event.getUrl()));
        vEvent.setLocation(event.getLocation());
        vEvent.setUrl(event.getUrl());

        if (event.getLocation().equalsIgnoreCase("online") || event.getLocation().equalsIgnoreCase("virtuell")) {
            String link = String.format("%s/webinar/%s", config.tenant().baseUrl(), eventId);
            vEvent.setLocation(link);
            vEvent.setUrl(link);
        }

        vEvent.setStatus(Status.confirmed());
        vEvent.setClassification(Classification.PUBLIC);
        vEvent.setOrganizer(new Organizer(config.tenant().name(), ""));

        ical.addEvent(vEvent);

        return Response
            .ok(Biweekly.write(ical).go())
            .header("Content-Disposition", "attachment; filename=\"jugda_" + eventId + ".ics\"")
            .build();
    }
}
