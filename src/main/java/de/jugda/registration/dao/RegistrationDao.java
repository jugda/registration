package de.jugda.registration.dao;

import de.jugda.registration.model.Registration;

import java.util.List;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
public interface RegistrationDao {

    Registration find(Registration registration);

    void save(Registration registration);

    List<Registration> findAll();

    List<Registration> findByEventId(String eventId);

    Registration findByEventIdAndEmail(String eventId, String email);

    int getCount(String eventId);
}
