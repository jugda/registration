package de.jugda.registration;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import de.jugda.registration.model.RequestParam;
import lombok.extern.log4j.Log4j;

import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Log4j
public class RegistrationHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        String method = request.getHttpMethod();
        String body = request.getBody();
        Map<String, String> queryParams = request.getQueryStringParameters();

        log.info(String.format("Received EventId: %s (%s)", queryParams.get("eventId"), body));

        String response = "";

        if (RequestParam.GET.equals(method)) {
            response = BeanFactory.getFormService().registrationForm(queryParams);
        } else if (RequestParam.POST.equals(method)) {
            response = BeanFactory.getRegistrationService().handleRequest(body);
        }

        return new APIGatewayProxyResponseEvent()
            .withStatusCode(200)
            .withHeaders(RequestParam.HEADER)
            .withBody(response);
    }

}
