package de.jugda.registration;

import de.jugda.registration.dao.DynamoDBDao;
import de.jugda.registration.dao.RegistrationDao;

import javax.enterprise.inject.Produces;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
public class BeanFactory {
    @Produces
    public RegistrationDao registrationDao() {
        return DynamoDBDao.instance();
    }
}
