package de.jugda.registration.service;

import de.jugda.registration.BeanFactory;
import de.jugda.registration.dao.RegistrationDao;
import de.jugda.registration.model.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
public class FormService {

    public String handleRequest(Map<String, String> queryParams) {
        String eventId = queryParams.getOrDefault(RequestParam.EVENT_ID, "dummy");
        String limit = queryParams.getOrDefault(RequestParam.LIMIT, "80");
        String deadline = queryParams.getOrDefault(RequestParam.DEADLINE, "2099-12-31T23:59:59");

        HandlebarsService handlebarsService = BeanFactory.getHandlebarsService();
        String response;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadlineTime = LocalDateTime.parse(deadline, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        if (now.isAfter(deadlineTime)) {
            // sorry, no registration for you
            response = handlebarsService.getRegistrationClosed();
        } else {
            // you are welcome to register
            RegistrationDao registrationDao = BeanFactory.getRegistrationDao();

            int maxCount = Integer.parseInt(limit);
            int actualCount = registrationDao.getRegistrationCount(eventId);
            if (actualCount >= maxCount) {
                response = handlebarsService.getRegistrationFull();
            } else {
                int freeSeats = maxCount - actualCount;
                Map<String, String> model = new HashMap<>();
                model.put(RequestParam.EVENT_ID, eventId);
                model.put("freeSeats", Integer.toString(freeSeats));
                response = handlebarsService.getRegistrationForm(model);
            }
        }

        return response;
    }

}
