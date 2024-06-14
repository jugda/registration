package de.jugda.registration.service;

import de.jugda.registration.dao.RegistrationDao;
import de.jugda.registration.model.Registration;
import de.jugda.registration.model.RegistrationForm;
import de.jugda.registration.slack.SlackWebClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@ApplicationScoped
public class RegistrationService {

    @Inject
    RegistrationDao registrationDao;
    @Inject
    EmailService emailService;
    @Inject
    SlackWebClient slack;

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

        // registration may be null when retrieved, due to eventual consistency
        // try to get it some time later...
        Registration savedRegistration;
        AtomicInteger counter = new AtomicInteger();
        do {
            savedRegistration = registrationDao.find(registration);
            if (savedRegistration == null) {
                try {
                    //noinspection BusyWait
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                counter.incrementAndGet();
            }
        } while (savedRegistration == null && counter.intValue() < 3);

        if (savedRegistration != null) {
            model.setId(savedRegistration.getId());
            emailService.sendRegistrationConfirmation(savedRegistration);
        }

        notifySlack(registration.getEventId(), model.getLimit());

        return model;
    }

    private void notifySlack(String eventId, int limit) {
        int registrationCount = getRegistrationCount(eventId);
        if (((float) registrationCount / (float) limit) >= 0.9) {
            String message = String.format(":bangbang: Event %1$s hat mehr als 90%% Anmeldungen (%2$d):\nhttps://registration.jug-da.de/admin/events/%1$s",
                eventId, registrationCount);
            slack.postMessage(message, System.getenv("SLACK_CHANNEL_GENERAL"));
        }
    }

}
