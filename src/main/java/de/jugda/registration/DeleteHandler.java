package de.jugda.registration;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import de.jugda.registration.model.RequestParam;
import lombok.extern.log4j.Log4j;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Log4j
public class DeleteHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest request, Context context) {
        String method = request.getHttpMethod();

        String response = "";

        switch (method) {
            case RequestParam.GET:
                response = BeanFactory.getFormService().deregistrationForm(request.getQueryStringParameters());
                break;

            case RequestParam.POST:
                response = BeanFactory.getDeleteService().deleteFromUi(request.getBody());
                break;

            case RequestParam.DELETE:
                String id = request.getQueryStringParameters().get(RequestParam.ID);
                BeanFactory.getDeleteService().deleteFromRequest(id);
                break;
        }

        return new AwsProxyResponse(200, RequestParam.HEADER, response);
    }

}
