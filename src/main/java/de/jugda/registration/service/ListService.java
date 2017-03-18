package de.jugda.registration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.jugda.registration.BeanFactory;
import de.jugda.registration.dao.RegistrationDao;
import de.jugda.registration.model.Registration;
import de.jugda.registration.model.RequestParam;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
public class ListService {

    @SneakyThrows
    public String handleRequest(String eventId, String type) {
        RegistrationDao registrationDao = BeanFactory.getRegistrationDao();
        List<Registration> registrations = registrationDao.getRegistrations(eventId);

        Map<String, Object> model = new HashMap<>();
        model.put(RequestParam.EVENT_ID, eventId);
        model.put(RequestParam.REGISTRATIONS, registrations);

        if ("json".equalsIgnoreCase(type)) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(model);
        } else {
            HandlebarsService handlebarsService = BeanFactory.getHandlebarsService();
            return handlebarsService.getRegistrationsList(model);
        }
    }

}
