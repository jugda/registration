package de.jugda.registration;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import de.jugda.registration.model.RequestParam;
import lombok.extern.log4j.Log4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Log4j
public class ListHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest request, Context context) {
        Map<String, String> queryParams = request.getQueryStringParameters();
        Map<String, String> header = RequestParam.HEADER;

        if (!isSecretValid(queryParams)) {
            return new AwsProxyResponse(403, header, "");
        }

        String response;
        if (queryParams.containsKey(RequestParam.EVENT_ID)) {
            String eventId = queryParams.getOrDefault(RequestParam.EVENT_ID, "dummy");
            String type = queryParams.getOrDefault(RequestParam.TYPE, "");
            response = BeanFactory.getListService().singleEvent(eventId, type);
            if ("json".equalsIgnoreCase(type)) {
                header = buildCorsHeader("application/json");
            }
            if ("namesOnly".equalsIgnoreCase(type)) {
                header = buildCorsHeader("text/plain");
            }
        } else {
            response = BeanFactory.getListService().allEvents();
        }

        return new AwsProxyResponse(200, header, response);
    }

    private boolean isSecretValid(Map<String, String> queryParams) {
        String secret = queryParams.getOrDefault(RequestParam.SECRET, "");
        return System.getenv("REGISTRATION_SECRET").equals(secret);
    }

    private Map<String, String> buildCorsHeader(String contentType) {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", contentType + "; charset=utf-8");
        header.put("Access-Control-Allow-Origin", "*");
        return header;
    }

}
