package de.jugda.registration;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import de.jugda.registration.model.RequestParam;
import lombok.extern.log4j.Log4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Log4j
public class ListHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        Map<String, String> queryParams = request.getQueryStringParameters();
        Map<String, String> header = RequestParam.HEADER;

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

        return new APIGatewayProxyResponseEvent().withStatusCode(200).withHeaders(header).withBody(response);
    }

    private Map<String, String> buildCorsHeader(String contentType) {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", contentType + "; charset=utf-8");
        header.put("Access-Control-Allow-Origin", "*");
        return header;
    }

}
