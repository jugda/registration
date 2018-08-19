package de.jugda.registration.service;

import de.jugda.registration.BeanFactory;
import de.jugda.registration.dao.RegistrationDao;
import de.jugda.registration.model.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
public class FormService {

    public String registrationForm(Map<String, String> queryParams) {
        String eventId = queryParams.getOrDefault(RequestParam.EVENT_ID, "dummy");
        String limit = queryParams.getOrDefault(RequestParam.LIMIT, "60");
        String deadline = queryParams.getOrDefault(RequestParam.DEADLINE, eventId + "T18:00:00+02:00");
        boolean showPub = Boolean.parseBoolean(queryParams.getOrDefault(RequestParam.SHOW_PUB, "true"));

        HandlebarsService handlebarsService = BeanFactory.getHandlebarsService();
        String response;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadlineTime = LocalDateTime.parse(deadline, DateTimeFormatter.ISO_DATE_TIME);
        LocalDate startDate = LocalDate.parse(eventId).minusMonths(1);

        if (now.isAfter(deadlineTime)) {
            // sorry, no registration for you
            response = handlebarsService.getRegistrationClosed();
        } else {
            // you are welcome to register
            RegistrationDao registrationDao = BeanFactory.getRegistrationDao();

            int maxCount = Integer.parseInt(limit);
            int actualCount = registrationDao.getCount(eventId);
            if (actualCount >= maxCount) {
                response = handlebarsService.getRegistrationFull(createModelMap(eventId));
            } else if (now.toLocalDate().isBefore(startDate)) {
                Map<String, Object> model = createModelMap(eventId);
                model.put("startDate", startDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                response = handlebarsService.getRegistrationNotYetOpen(model);
            } else {
                int freeSeats = maxCount - actualCount;
                Map<String, Object> model = createModelMap(eventId);
                model.put("freeSeats", Integer.toString(freeSeats));
                model.put("actualCount", Integer.toString(actualCount));
                model.put("limit", limit);
                model.put("showPub", showPub);
                response = handlebarsService.getRegistrationForm(model);
            }
        }

        return response;
    }

    private Map<String, Object> createModelMap(String eventId) {
        Map<String, Object> model = new HashMap<>();
        model.put(RequestParam.EVENT_ID, eventId);
        return model;
    }

    public String deregistrationForm(Map<String, String> queryParams) {
        String eventId = queryParams.getOrDefault(RequestParam.EVENT_ID, "dummy");
        Map<String, Object> model = Collections.singletonMap(RequestParam.EVENT_ID, eventId);
        return BeanFactory.getHandlebarsService().getDeregistration(model);
    }

}
