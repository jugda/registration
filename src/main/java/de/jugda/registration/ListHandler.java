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
public class ListHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        Map<String, String> queryParams = request.getQueryStringParameters();
        Map<String, String> header = RequestParam.HEADER;

        String response;
        if (null != queryParams && queryParams.containsKey(RequestParam.EVENT_ID)) {
            String eventId = queryParams.getOrDefault(RequestParam.EVENT_ID, "dummy");
            response = BeanFactory.getListService().singleEvent(eventId);
        } else {
            response = BeanFactory.getListService().allEvents();
        }

        return new APIGatewayProxyResponseEvent().withStatusCode(200).withHeaders(header).withBody(response);
    }

}
