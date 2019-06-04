package de.jugda.registration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.jugda.registration.BeanFactory;
import de.jugda.registration.dao.RegistrationDao;
import de.jugda.registration.model.Registration;
import de.jugda.registration.model.RequestParam;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
public class ListService {

    private final ObjectMapper mapper;

    public ListService() {
        this.mapper = new ObjectMapper();
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @SneakyThrows
    public String singleEvent(String eventId, String type) {
        RegistrationDao registrationDao = BeanFactory.getRegistrationDao();
        List<Registration> registrations = registrationDao.findByEventId(eventId);

        Map<String, Object> model = new HashMap<>();
        model.put(RequestParam.EVENT_ID, eventId);
        model.put(RequestParam.REGISTRATIONS, registrations);
        model.put("total", registrations.size());

        if ("json".equalsIgnoreCase(type)) {
            return mapper.writeValueAsString(model);
        } else if ("namesOnly".equalsIgnoreCase(type)) {
            return registrations.stream().map(Registration::getName).collect(Collectors.joining("\n"));
        } else {
            HandlebarsService handlebarsService = BeanFactory.getHandlebarsService();
            return handlebarsService.getRegistrationsList(model);
        }
    }

    public String allEvents() {
        List<Registration> registrations = BeanFactory.getRegistrationDao().findAll();

        Map<String, Integer> events = new LinkedHashMap<>();
        registrations.forEach(reg -> events.put(reg.getEventId(), events.getOrDefault(reg.getEventId(), 0) + 1));

        Map<String, Object> model = new HashMap<>();
        model.put("events", events);
        return BeanFactory.getHandlebarsService().getRegistrationsOverview(model);
    }

}
