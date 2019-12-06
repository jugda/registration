package de.jugda.registration.service;

import de.jugda.registration.dao.RegistrationDao;
import de.jugda.registration.model.Registration;
import de.jugda.registration.model.RegistrationForm;
import de.jugda.registration.slack.SlackWebClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@ApplicationScoped
public class RegistrationService {

    @Inject
    RegistrationDao registrationDao;
    @Inject
    EmailService emailService;

    public int getRegistrationCount(String eventId) {
        return registrationDao.getCount(eventId);
    }

    public RegistrationForm handleRegistration(RegistrationForm model) {
        Registration registration = Registration.of(model);

        Registration existingRegistration = registrationDao.find(registration);
        if (null != existingRegistration) {
            registration.setId(existingRegistration.getId());
        }

        registrationDao.save(registration);
        registration = registrationDao.find(registration);
        model.setId(registration.getId());

        emailService.sendRegistrationConfirmation(registration);

        notifySlack(registration.getEventId(), model.getLimit());

        return model;
    }

    private void notifySlack(String eventId, int limit) {
        int registrationCount = getRegistrationCount(eventId);
        if (((float) registrationCount / (float) limit) >= 0.9) {
            SlackWebClient slack = new SlackWebClient(System.getenv("SLACK_OAUTH_ACCESS_TOKEN"));
            String message = String.format(":bangbang: Event %1$s hat mehr als 90%% Anmeldungen (%2$d):\nhttps://registration.jug-da.de/list?eventId=%1$s",
                eventId, registrationCount);
            slack.postMessage(message, System.getenv("SLACK_CHANNEL_GENERAL"));
        }
    }

//    private boolean isValid(Map<String, Object> model) {
//        boolean valid = true;
//        String name = model.getOrDefault(RequestParam.NAME, "").toString();
//        if ("".equals(name.trim())) {
//            model.put("nameError", "true");
//            valid = false;
//        }
//        String email = model.getOrDefault(RequestParam.EMAIL, "").toString();
//        if ("".equals(email.trim())) {
//            model.put("emailError", "true");
//            valid = false;
//        }
//
//        return valid;
//    }

}
