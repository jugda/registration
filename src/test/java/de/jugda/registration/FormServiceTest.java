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
        when(registrationDao.getRegistrationCount(anyString())).thenReturn(0);

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

        String response = formService.handleRequest(queryParams);
        log.info(response);

        assertTrue(response.contains("<form method=\"post\""));
    }

    @Test
    public void testFormRequest_defaultDeadline() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("eventId", "2099-12-31");
        queryParams.put("limit", "80");

        String response = formService.handleRequest(queryParams);
        log.info(response);

        assertTrue(response.contains("<form method=\"post\""));

    }

    @Test
    public void testFormRequest_limitSucceeded() {
        when(registrationDao.getRegistrationCount(anyString())).thenReturn(80);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("eventId", "2017-03-16");
        queryParams.put("limit", "80");
        queryParams.put("deadline", "2099-12-31T23:59:59");

        String response = formService.handleRequest(queryParams);
        log.info(response);

        assertTrue(response.contains("Für diese Veranstaltung sind alle verfügbaren Plätze bereits belegt."));

    }

    @Test
    public void testFormRequest_closed() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("eventId", "2017-03-16");
        queryParams.put("limit", "80");
        queryParams.put("deadline", "2016-12-31T23:59:59");

        String response = formService.handleRequest(queryParams);
        log.info(response);

        assertTrue(response.contains("Für diese Veranstaltung ist die Anmeldefrist leider schon abgelaufen."));
    }

}
