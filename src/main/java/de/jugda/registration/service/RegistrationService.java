package de.jugda.registration.service;

import de.jugda.registration.BeanFactory;
import de.jugda.registration.dao.RegistrationDao;
import de.jugda.registration.model.Registration;
import de.jugda.registration.model.RequestParam;
import de.jugda.registration.slack.SlackWebClient;

import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
public class RegistrationService {

    public String handleRequest(String body) {
        Map<String, Object> model = RequestParam.parseBody(body);

        HandlebarsService handlebarsService = BeanFactory.getHandlebarsService();
        String response;

        if (isValid(model)) {
            Registration registration = Registration.of(model);
            RegistrationDao registrationDao = BeanFactory.getRegistrationDao();

            Registration existingRegistration = registrationDao.find(registration);
            if (null != existingRegistration) {
                registration.setId(existingRegistration.getId());
            }

            registrationDao.save(registration);
            registration = registrationDao.find(registration);
            model.put("id", registration.getId());

            BeanFactory.getEmailService().sendRegistrationConfirmation(registration);

            notifySlack(model, registration, registrationDao);

            response = handlebarsService.getThanksPage(model);
        } else {
            response = handlebarsService.getRegistrationForm(model);
        }

        return response;
    }

    private void notifySlack(Map<String, Object> model, Registration registration, RegistrationDao registrationDao) {
        int registrationCount = registrationDao.getCount(registration.getEventId());
        if (((float) registrationCount / Float.parseFloat(model.get("limit").toString())) >= 0.9) {
            SlackWebClient slack = new SlackWebClient(System.getenv("SLACK_OAUTH_ACCESS_TOKEN"));
            String message = String.format(":bangbang: Event %1$s hat mehr als 90%% Anmeldungen (%2$d):\nhttps://registration.jug-da.de/list?eventId=%1$s",
                registration.getEventId(), registrationCount);
            slack.postMessage(message, System.getenv("SLACK_CHANNEL_GENERAL"));
        }
    }

    private boolean isValid(Map<String, Object> model) {
        boolean valid = true;
        String name = model.getOrDefault(RequestParam.NAME, "").toString();
        if ("".equals(name.trim())) {
            model.put("nameError", "true");
            valid = false;
        }
        String email = model.getOrDefault(RequestParam.EMAIL, "").toString();
        if ("".equals(email.trim())) {
            model.put("emailError", "true");
            valid = false;
        }

        return valid;
    }

}
