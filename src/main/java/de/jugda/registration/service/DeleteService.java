package de.jugda.registration.service;

import de.jugda.registration.dao.RegistrationDao;
import de.jugda.registration.model.DeregistrationForm;
import de.jugda.registration.model.Registration;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@ApplicationScoped
public class DeleteService {

    @Inject
    RegistrationDao registrationDao;
    @Inject
    EmailService emailService;

    public String deleteFromUi(DeregistrationForm form) {
        Registration registration = new Registration();
        registration.setEventId(form.getEventId());
        registration.setEmail(form.getEmail().toLowerCase());
        registration = registrationDao.find(registration);
        return deleteFromUri(registration.getId());
    }

    public String deleteFromUri(String id) {
        String name = "";
        try {
            Registration registration = registrationDao.delete(id);
            if (!registration.isWaitlist()) {
                processWaitlist(registration.getEventId());
            }
            name = registration.getName();
        } catch (Exception e) {
            // intended
        }
        return name;
    }

    public void delete(String id) {
        registrationDao.delete(id);
    }

    private void processWaitlist(String eventId) {
        List<Registration> waitlist = registrationDao.findWaitlistByEventId(eventId);
        if (!waitlist.isEmpty()) {
            Registration waiter = waitlist.getFirst();
            waiter.setWaitlist(false);
            registrationDao.save(waiter);
            emailService.sendWaitlistToAttendeeConfirmation(waiter);
        }
    }

}
