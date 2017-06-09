package de.jugda.registration.service;

import de.jugda.registration.BeanFactory;
import de.jugda.registration.dao.RegistrationDao;
import de.jugda.registration.model.Registration;
import de.jugda.registration.model.RequestParam;
import de.jugda.registration.slack.SlackWebClient;
import lombok.SneakyThrows;

import java.net.URLDecoder;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
public class RegistrationService {

    @SneakyThrows
    public String handleRequest(String body) {
        String decoded = URLDecoder.decode(body, RequestParam.ENCODING);

        Map<String, String> model = Arrays.stream(decoded.split("&")).map(this::splitQueryParameter)
            .collect(Collectors.toMap(AbstractMap.SimpleImmutableEntry::getKey, AbstractMap.SimpleImmutableEntry::getValue));

        HandlebarsService handlebarsService = BeanFactory.getHandlebarsService();
        String response;

        if (isValid(model)) {
            Registration registration = Registration.of(model);
            RegistrationDao registrationDao = BeanFactory.getRegistrationDao();

            Registration existingRegistration = registrationDao.find(registration);
            if (null != existingRegistration) {
                registration.setId(existingRegistration.getId());
            }

            registrationDao.save(registration);

            int registrationCount = registrationDao.getCount(registration.getEventId());
            if (registrationCount / Integer.parseInt(model.get("limit")) >= 0.95) {
                SlackWebClient slack = new SlackWebClient(System.getenv("SLACK_OAUTH_ACCESS_TOKEN"));
                String message = String.format(":bangbang: Event %1$s hat mehr als 95%% Anmeldungen (%2$d):\nhttps://registration.jug-da.de/list?eventId=%1$s",
                    registration.getEventId(), registrationCount);
                slack.postMessage(message, System.getenv("SLACK_CHANNEL_GENERAL"));
            }

            response = handlebarsService.getThanksPage(model);
        } else {
            response = handlebarsService.getRegistrationForm(model);
        }

        return response;
    }

    private AbstractMap.SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
        String[] parts = it.split("=");
        final String key = parts[0];
        final String value = parts.length > 1 ? parts[1].trim() : "";
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

    private boolean isValid(Map<String, String> model) {
        boolean valid = true;
        String name = model.getOrDefault(RequestParam.NAME, "");
        if ("".equals(name.trim())) {
            model.put("nameError", "true");
            valid = false;
        }
        String email = model.getOrDefault(RequestParam.EMAIL, "");
        if ("".equals(email.trim())) {
            model.put("emailError", "true");
            valid = false;
        }

        return valid;
    }

}
