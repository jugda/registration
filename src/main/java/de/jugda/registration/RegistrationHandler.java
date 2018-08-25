package de.jugda.registration;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import de.jugda.registration.model.RequestParam;
import lombok.extern.log4j.Log4j;

import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Log4j
public class RegistrationHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest request, Context context) {
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

        return new AwsProxyResponse(200, RequestParam.HEADER, response);
    }

}
