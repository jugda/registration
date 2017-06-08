package de.jugda.registration;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import de.jugda.registration.model.RequestParam;
import lombok.extern.log4j.Log4j;

import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Log4j
public class RequestHandler implements com.amazonaws.services.lambda.runtime.RequestHandler<AwsProxyRequest, AwsProxyResponse> {

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest request, Context context) {
        String method = request.getHttpMethod();
        String path = request.getPath();
        String body = request.getBody();
        Map<String, String> queryParams = request.getQueryStringParameters();

        log.info(String.format("Received Event: %s %s, EventId: %s (%s)", method, path, queryParams.get("eventId"), body));

        Map<String, String> header = RequestParam.HEADER;
        String response = "";

        if ("/registration".equals(path)) {
            if (RequestParam.GET.equals(method)) {
                response = BeanFactory.getFormService().handleRequest(queryParams);
            } else if (RequestParam.POST.equals(method)) {
                response = BeanFactory.getRegistrationService().handleRequest(body);
            }
        } else if ("/list".equals(path)) {
            if (RequestParam.GET.equals(method) && isSecretValid(request)) {
                String eventId = queryParams.getOrDefault(RequestParam.EVENT_ID, "dummy");
                String type = queryParams.getOrDefault(RequestParam.TYPE, "");
                response = BeanFactory.getListHandler().handleRequest(eventId, type);
                if ("json".equalsIgnoreCase(type)) {
                    header = RequestParam.HEADER_JSON;
                }
            }
        }

        return new AwsProxyResponse(200, header, response);
    }

    private boolean isSecretValid(AwsProxyRequest request) {
        String secret = request.getQueryStringParameters().getOrDefault("secret", "");
        return System.getenv("REGISTRATION_SECRET").equals(secret);
    }

}
