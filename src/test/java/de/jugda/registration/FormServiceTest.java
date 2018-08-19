package de.jugda.registration;

import de.jugda.registration.dao.RegistrationDao;
import de.jugda.registration.service.FormService;
import de.jugda.registration.service.HandlebarsService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Niko Köbler (for msg systems ag) 2017
 */
@Slf4j
@RunWith(PowerMockRunner.class)
@PrepareForTest(BeanFactory.class)
public class FormServiceTest {

    private FormService formService = new FormService();
    private RegistrationDao registrationDao;
    private HandlebarsService handlebarsService = new HandlebarsService();

    @Before
    public void before() {
        registrationDao = mock(RegistrationDao.class);
        when(registrationDao.getCount(anyString())).thenReturn(0);

        PowerMockito.mockStatic(BeanFactory.class);
        when(BeanFactory.getRegistrationDao()).thenReturn(registrationDao);
        when(BeanFactory.getHandlebarsService()).thenReturn(handlebarsService);
    }

    @Test
    public void testFormRequest() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("eventId", "2017-03-16");
        queryParams.put("limit", "80");
        queryParams.put("deadline", "2099-12-31T23:59:59");

        String response = formService.registrationForm(queryParams);
        log.info(response);

        assertTrue(response.contains("<form method=\"post\""));
    }

    @Test
    public void testFormRequest_defaultDeadline() {
        String eventId = LocalDate.now().plusMonths(1).minusDays(1).format(DateTimeFormatter.ISO_DATE);
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("eventId", eventId);
        queryParams.put("limit", "80");

        String response = formService.registrationForm(queryParams);
        log.info(response);

        assertTrue(response.contains("<form method=\"post\""));

    }

    @Test
    public void testFormRequest_notOpenYet() {
        String eventId = LocalDate.now().plusMonths(1).plusDays(1).format(DateTimeFormatter.ISO_DATE);
        LocalDate startDate = LocalDate.parse(eventId).minusMonths(1);
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("eventId", eventId);
        queryParams.put("limit", "80");

        String response = formService.registrationForm(queryParams);
        log.info(response);

        assertTrue(response.contains(String.format("Für diese Veranstaltung startet die Anmeldung erst am %s.", startDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))));
    }

    @Test
    public void testFormRequest_limitSucceeded() {
        when(registrationDao.getCount(anyString())).thenReturn(80);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("eventId", "2017-03-16");
        queryParams.put("limit", "80");
        queryParams.put("deadline", "2099-12-31T23:59:59");

        String response = formService.registrationForm(queryParams);
        log.info(response);

        assertTrue(response.contains("Für diese Veranstaltung sind alle verfügbaren Plätze bereits belegt."));
        assertTrue(response.contains("Bitte meldet Euch bei uns per <a href=\"mailto:orga@jug-da.de\">E-Mail</a>"));
        assertTrue(response.contains("Du bist bereits angemeldet und willst Dich abmelden oder Deine Daten löschen?"));
        assertTrue(response.contains("<a href=\"/delete?eventId=2017-03-16\">Dann klicke hier.</a>"));
    }

    @Test
    public void testFormRequest_closed() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("eventId", "2017-03-16");
        queryParams.put("limit", "80");
        queryParams.put("deadline", "2016-12-31T23:59:59");

        String response = formService.registrationForm(queryParams);
        log.info(response);

        assertTrue(response.contains("Für diese Veranstaltung ist die Anmeldefrist leider schon abgelaufen."));
    }

}
