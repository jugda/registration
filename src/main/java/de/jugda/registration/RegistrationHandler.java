package de.jugda.registration;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import de.jugda.registration.model.RequestParam;
import de.jugda.registration.dao.DynamoDBDao;
import de.jugda.registration.service.HandlebarsService;
import de.jugda.registration.dao.RegistrationDao;
import lombok.SneakyThrows;

import java.net.URLDecoder;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
public class RegistrationHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {

    @Override
    @SneakyThrows
    public AwsProxyResponse handleRequest(AwsProxyRequest request, Context context) {
        String body = request.getBody();
        String decoded = URLDecoder.decode(body, RequestParam.ENCODING);

        Map<String, String> model = Arrays.stream(decoded.split("&")).map(this::splitQueryParameter)
            .collect(Collectors.toMap(AbstractMap.SimpleImmutableEntry::getKey, AbstractMap.SimpleImmutableEntry::getValue));

        HandlebarsService handlebarsService = new HandlebarsService();
        String response;

        if (isValid(model)) {
            RegistrationDao registrationDao = DynamoDBDao.instance();
            registrationDao.saveRegistration(model);

            response = handlebarsService.getThanksPage(model);
        } else {
            response = handlebarsService.getRegistrationForm(model);
        }

        return new AwsProxyResponse(200, RequestParam.HEADER, response);
    }

    private AbstractMap.SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
        String[] parts = it.split("=");
        final String key = parts[0];
        final String value = parts.length > 1 ? parts[1] : "";
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
