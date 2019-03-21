package de.jugda.registration.service;

import de.jugda.registration.BeanFactory;
import de.jugda.registration.dao.RegistrationDao;
import de.jugda.registration.model.Registration;
import de.jugda.registration.model.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
public class DeleteService {

    public String deleteFromUi(String body) {
        Map<String, Object> model = RequestParam.parseBody(body);
        if (isValid(model)) {
            String eventId = model.get(RequestParam.EVENT_ID).toString();
            String email = model.get(RequestParam.EMAIL).toString().toLowerCase();

            RegistrationDao registrationDao = BeanFactory.getRegistrationDao();

            String name = "";
            try {
                Registration registration = registrationDao.findByEventIdAndEmail(eventId, email);
                registrationDao.delete(registration.getId());
                processWaitlist(registration.getEventId());
                name = registration.getName();
            } catch (Exception e) {
                // intended
            }

            return BeanFactory.getHandlebarsService().getDeregistrationThanks(Collections.singletonMap("name", name));
        } else {
            return BeanFactory.getHandlebarsService().getDeregistration(model);
        }
    }

    public String deleteFromUri(String id) {
            String name = "";
            try {
                RegistrationDao registrationDao = BeanFactory.getRegistrationDao();
                Registration registration = registrationDao.delete(id);
                processWaitlist(registration.getEventId());
                name = registration.getName();
            } catch (Exception e) {
                // intended
            }

            return BeanFactory.getHandlebarsService().getDeregistrationThanks(Collections.singletonMap("name", name));
    }

    public void deleteFromRequest(String id) {
        BeanFactory.getRegistrationDao().delete(id);
    }

    @SuppressWarnings("Duplicates")
    private boolean isValid(Map<String, Object> model) {
        boolean valid = true;
        String email = model.getOrDefault(RequestParam.EMAIL, "").toString();
        if ("".equals(email.trim())) {
            model.put("emailError", "true");
            valid = false;
        }
        return valid;
    }

    private void processWaitlist(String eventId) {
        List<Registration> waitlist = BeanFactory.getRegistrationDao().findWaitlistByEventId(eventId);
        if (!waitlist.isEmpty()) {
            Registration waiter = waitlist.get(0);
            waiter.setWaitlist(false);
            BeanFactory.getRegistrationDao().save(waiter);
            BeanFactory.getEmailService().sendWaitlistToAttendeeConfirmation(waiter);
        }
    }

}
