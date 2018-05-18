package de.jugda.registration;

import de.jugda.registration.model.Registration;
import de.jugda.registration.model.RequestParam;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
public class RegistrationTest {
    @Test
    public void testStaticOfModel() {
        Map<String, Object> model = new HashMap<>();
        model.put(RequestParam.EVENT_ID, "2018-12-31");
        model.put(RequestParam.NAME, "John Doe");
        model.put(RequestParam.EMAIL, "john@doe.com");
        model.put(RequestParam.TWITTER, "@johndoe");
        model.put(RequestParam.PUB, "on");

        Registration reg = Registration.of(model);

        assertEquals("2018-12-31", reg.getEventId());
        assertEquals("John Doe", reg.getName());
        assertEquals("john@doe.com", reg.getEmail());
        assertEquals("@johndoe", reg.getTwitter());
        assertTrue(reg.isPub());
        assertEquals(1548892800L, reg.getTtl().longValue());
    }
}
