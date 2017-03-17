package de.jugda.registration;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import de.jugda.registration.model.RequestParam;
import de.jugda.registration.service.DynamoDBService;
import de.jugda.registration.service.HandlebarsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
public class FormHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest request, Context context) {
        Map<String, String> queryParams = request.getQueryStringParameters();
        String eventId = queryParams.getOrDefault(RequestParam.EVENT_ID, "dummy");
        String limit = queryParams.getOrDefault(RequestParam.LIMIT, "80");
        String deadline = queryParams.getOrDefault(RequestParam.DEADLINE, "");

        HandlebarsService handlebarsService = new HandlebarsService();
        String response;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadlineTime = LocalDateTime.parse(deadline, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        if (now.isAfter(deadlineTime)) {
            // sorry, no registration for you
            response = handlebarsService.getRegistrationClosed();
        } else {
            // you are welcome to register
            DynamoDBService dynamoDBService = new DynamoDBService();

            int maxCount = Integer.parseInt(limit);
            int actualCount = dynamoDBService.getRegistrationCount(eventId);
            if (actualCount >= maxCount) {
                response = handlebarsService.getRegistrationFull();
            } else {
                response = handlebarsService.getRegistrationForm(Collections.singletonMap(RequestParam.EVENT_ID, eventId));
            }
        }

        return new AwsProxyResponse(200, RequestParam.HEADER, response);
    }

}
