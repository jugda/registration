package de.jugda.registration;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.extern.log4j.Log4j;

import java.util.Collections;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Log4j
public class FormHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest request, Context context) {
        return new AwsProxyResponse(200, Collections.singletonMap("Content-Type", "text/plain"), "Hello!");
    }

}
