package de.jugda.registration;

import de.jugda.registration.dao.DynamoDBDao;
import de.jugda.registration.dao.RegistrationDao;
import de.jugda.registration.service.DeleteService;
import de.jugda.registration.service.EmailService;
import de.jugda.registration.service.FormService;
import de.jugda.registration.service.HandlebarsService;
import de.jugda.registration.service.ListService;
import de.jugda.registration.service.RegistrationService;
import lombok.experimental.UtilityClass;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@UtilityClass
public class BeanFactory {

    private static FormService formService;
    private static RegistrationService registrationService;
    private static ListService listService;
    private static DeleteService deleteService;
    private static RegistrationDao registrationDao;
    private static HandlebarsService handlebarsService;
    private static EmailService emailService;

    public static FormService getFormService() {
        if (formService == null) {
            formService = new FormService();
        }
        return formService;
    }

    public static RegistrationService getRegistrationService() {
        if (registrationService == null) {
            registrationService = new RegistrationService();
        }
        return registrationService;
    }

    public static ListService getListService() {
        if (listService == null) {
            listService = new ListService();
        }
        return listService;
    }

    public static DeleteService getDeleteService() {
        if (deleteService == null) {
            deleteService = new DeleteService();
        }
        return deleteService;
    }

    public static RegistrationDao getRegistrationDao() {
        if (registrationDao == null) {
            registrationDao = DynamoDBDao.instance();
        }
        return registrationDao;
    }

    public static HandlebarsService getHandlebarsService() {
        if (handlebarsService == null) {
            handlebarsService = new HandlebarsService();
        }
        return handlebarsService;
    }

    public static EmailService getEmailService() {
        if (emailService == null) {
            emailService = new EmailService();
        }
        return emailService;
    }

}
