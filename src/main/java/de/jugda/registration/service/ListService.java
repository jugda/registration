package de.jugda.registration.service;

import de.jugda.registration.dao.RegistrationDao;
import de.jugda.registration.model.Registration;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@ApplicationScoped
public class ListService {

    @Inject
    RegistrationDao registrationDao;

    public List<Registration> singleEventRegistrations(String eventId) {
        return registrationDao.findByEventId(eventId);
    }

    public Map<String, Integer> allEvents() {
        List<Registration> registrations = registrationDao.findAll();

        Map<String, Integer> events = new LinkedHashMap<>();
        registrations.forEach(reg -> events.put(reg.getEventId(), events.getOrDefault(reg.getEventId(), 0) + 1));

        return events;
    }

}
