package de.jugda.registration;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import de.jugda.registration.model.RequestParam;
import lombok.extern.log4j.Log4j;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Log4j
public class DeleteHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        String method = request.getHttpMethod();
        String id = request.getQueryStringParameters().get(RequestParam.ID);

        String response = "";

        switch (method) {
            case RequestParam.GET:
                if (id != null) {
                    response = BeanFactory.getDeleteService().deleteFromUri(id);
                } else {
                    response = BeanFactory.getFormService().deregistrationForm(request.getQueryStringParameters());
                }
                break;

            case RequestParam.POST:
                response = BeanFactory.getDeleteService().deleteFromUi(request.getBody());
                break;

            case RequestParam.DELETE:
                BeanFactory.getDeleteService().deleteFromRequest(id);
                break;
        }

        return new APIGatewayProxyResponseEvent()
            .withStatusCode(200)
            .withHeaders(RequestParam.HEADER)
            .withBody(response);
    }

}
