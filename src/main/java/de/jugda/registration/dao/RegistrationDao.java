package de.jugda.registration.dao;

import de.jugda.registration.model.Registration;

import java.util.List;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
public interface RegistrationDao {

    Registration findRegistration(Registration registration);

    void saveRegistration(Registration registration);

    List<Registration> findAll();

    List<Registration> getRegistrations(String eventId);

    int getRegistrationCount(String eventId);
}
