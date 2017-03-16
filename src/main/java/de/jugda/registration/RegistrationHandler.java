package de.jugda.registration;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import de.jugda.registration.service.DynamoDBService;
import de.jugda.registration.service.HandlebarsService;
import lombok.SneakyThrows;

import java.net.URLDecoder;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
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
        String decoded = URLDecoder.decode(body, "ISO-8859-1");

        Map<String, String> model = Arrays.stream(decoded.split("&")).map(this::splitQueryParameter)
            .collect(Collectors.toMap(AbstractMap.SimpleImmutableEntry::getKey, AbstractMap.SimpleImmutableEntry::getValue));

        DynamoDBService dynamoDBService = new DynamoDBService();
        dynamoDBService.saveRegistration(model);

        HandlebarsService handlebarsService = new HandlebarsService();
        String response = handlebarsService.getThanksPage(model);

        return new AwsProxyResponse(200, Collections.singletonMap("Content-Type", "text/html"), response);
    }

    private AbstractMap.SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
        String[] parts = it.split("=");
        final String key = parts[0];
        final String value = parts.length > 1 ? parts[1] : "";
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

}
