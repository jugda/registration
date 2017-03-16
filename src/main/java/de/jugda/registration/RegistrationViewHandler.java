package de.jugda.registration;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import de.jugda.registration.model.Registration;
import de.jugda.registration.service.DynamoDBService;
import de.jugda.registration.service.HandlebarsService;
import lombok.SneakyThrows;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
public class RegistrationViewHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {

    @Override
    @SneakyThrows
    public AwsProxyResponse handleRequest(AwsProxyRequest request, Context context) {
        String eventId = request.getQueryStringParameters().getOrDefault("eventId", "dummy");

        DynamoDBService dynamoDBService = new DynamoDBService();
        List<Registration> registrations = dynamoDBService.getRegistrations(eventId);

        Map<String, Object> model = new HashMap<>();
        model.put("eventId", eventId);
        model.put("registrations", registrations);

        HandlebarsService handlebarsService = new HandlebarsService();
        String response = handlebarsService.getRegistrationsPage(model);

        return new AwsProxyResponse(200, Collections.singletonMap("Content-Type", "text/html"), response);
    }

}
