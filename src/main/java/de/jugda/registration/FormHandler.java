package de.jugda.registration;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import de.jugda.registration.model.RegParam;
import de.jugda.registration.model.RequestParam;
import de.jugda.registration.service.HandlebarsService;

import java.util.Collections;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
public class FormHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest request, Context context) {
        Map<String, String> queryParams = request.getQueryStringParameters();
        String eventId = queryParams.getOrDefault(RegParam.EVENT_ID, "dummy");

        HandlebarsService handlebarsService = new HandlebarsService();
        String body = handlebarsService.getRegistrationForm(Collections.singletonMap(RegParam.EVENT_ID, eventId));

        return new AwsProxyResponse(200, RequestParam.HEADER, body);
    }

}
