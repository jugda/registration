package de.jugda.registration;

import de.jugda.registration.dao.DynamoDBDao;
import de.jugda.registration.dao.RegistrationDao;
import de.jugda.registration.service.FormService;
import de.jugda.registration.service.HandlebarsService;
import de.jugda.registration.service.RegistrationService;
import de.jugda.registration.service.ListService;
import lombok.Synchronized;
import lombok.experimental.UtilityClass;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@UtilityClass
public class BeanFactory {

    private static FormService formService;
    private static RegistrationService registrationService;
    private static ListService listHandler;
    private static RegistrationDao registrationDao;
    private static HandlebarsService handlebarsService;

    @Synchronized
    public static FormService getFormService() {
        if (formService == null) {
            formService = new FormService();
        }
        return formService;
    }

    @Synchronized
    public static RegistrationService getRegistrationService() {
        if (registrationService == null) {
            registrationService = new RegistrationService();
        }
        return registrationService;
    }

    @Synchronized
    public static ListService getListHandler() {
        if (listHandler == null) {
            listHandler = new ListService();
        }
        return listHandler;
    }

    @Synchronized
    public static RegistrationDao getRegistrationDao() {
        if (registrationDao == null) {
            registrationDao = DynamoDBDao.instance();
        }
        return registrationDao;
    }

    @Synchronized
    public static HandlebarsService getHandlebarsService() {
        if (handlebarsService == null) {
            handlebarsService = new HandlebarsService();
        }
        return handlebarsService;
    }

}
