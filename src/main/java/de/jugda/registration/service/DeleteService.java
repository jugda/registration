package de.jugda.registration.service;

import de.jugda.registration.BeanFactory;
import de.jugda.registration.dao.RegistrationDao;
import de.jugda.registration.model.Registration;
import de.jugda.registration.model.RequestParam;

import java.util.Collections;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
public class DeleteService {

    public String deleteFromUi(String body) {
        Map<String, String> model = RequestParam.parseBody(body);
        if (isValid(model)) {
            String eventId = model.get(RequestParam.EVENT_ID);
            String email = model.get(RequestParam.EMAIL).toLowerCase();

            RegistrationDao registrationDao = BeanFactory.getRegistrationDao();

            Registration registration = registrationDao.findByEventIdAndEmail(eventId, email);
            registrationDao.delete(registration.getId());

            return BeanFactory.getHandlebarsService().getDeregistrationThanks(Collections.singletonMap("name", registration.getName()));
        } else {
            return BeanFactory.getHandlebarsService().getDeregistration(model);
        }
    }

    public void deleteFromRequest(String id) {
        BeanFactory.getRegistrationDao().delete(id);
    }

    private boolean isValid(Map<String, String> model) {
        boolean valid = true;
        String email = model.getOrDefault(RequestParam.EMAIL, "");
        if ("".equals(email.trim())) {
            model.put("emailError", "true");
            valid = false;
        }
        return valid;
    }

}
