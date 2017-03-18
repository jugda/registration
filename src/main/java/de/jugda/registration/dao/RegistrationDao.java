package de.jugda.registration.dao;

import de.jugda.registration.model.Registration;

import java.util.List;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
public interface RegistrationDao {

    void saveRegistration(Map<String, String> model);

    List<Registration> getRegistrations(String eventId);

    int getRegistrationCount(String eventId);
}
