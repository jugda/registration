package de.jugda.registration;

import de.jugda.registration.dao.RegistrationDao;
import de.jugda.registration.model.Registration;
import de.jugda.registration.service.HandlebarsService;
import de.jugda.registration.service.ListService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
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
public class ListServiceTest {

    private ListService listService = new ListService();
    private RegistrationDao registrationDao;
    private HandlebarsService handlebarsService = new HandlebarsService();

    @Before
    public void before() {
        Registration reg1 = getRegistration("John Doe", "john@doe.com", "@johndoe", "4711");
        Registration reg2 = getRegistration("Dieter Develop", "dieter@develop.com", "@ddevelop", "4711");

        registrationDao = mock(RegistrationDao.class);
        when(registrationDao.findByEventId(anyString())).thenReturn(Arrays.asList(reg1, reg2));
        when(registrationDao.findAll()).thenReturn(Arrays.asList(reg1, reg2));

        PowerMockito.mockStatic(BeanFactory.class);
        when(BeanFactory.getRegistrationDao()).thenReturn(registrationDao);
        when(BeanFactory.getHandlebarsService()).thenReturn(handlebarsService);
    }

    private Registration getRegistration(String name, String email, String twitter, String eventId) {
        Registration reg = new Registration();
        reg.setId(UUID.randomUUID().toString());
        reg.setName(name);
        reg.setEmail(email);
        reg.setTwitter(twitter);
        reg.setEventId(eventId);
        reg.setCreated(new Date());
        return reg;
    }

    @Test
    public void testListRequest_html() {
        String response = listService.singleEvent("4711", "");
        log.info(response);

        assertTrue(response.contains("<h2>JUG DA Anmeldungen für EventId 4711: 2</h2>"));
    }

    @Test
    public void testListRequest_json() {
        String response = listService.singleEvent("4711", "json");
        log.info(response);

        assertTrue(response.contains("\"eventId\":\"4711\""));
    }

    @Test
    public void testListRequest_namesOnly() {
        final String response = listService.singleEvent("4711", "namesOnly");
        log.info(response);

        assertEquals("John Doe\nDieter Develop", response);
    }

    @Test
    public void testOverview() {
        String response = listService.allEvents();
        log.info(response);

        assertTrue(response.contains("eventId=4711"));
    }

}
