package de.jugda.registration;

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
public class AuthHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
//        log.info("Received Event: " + event);

        Map<String, Object> policy;

        if (isSecretValid(event)) {
            policy = generatePolicy("jugda-member", "Allow", event.get("methodArn").toString());
        } else {
            policy = generatePolicy("anonymous", "Deny", event.get("methodArn").toString());
        }

        return policy;
    }

    @SuppressWarnings("unchecked")
    private boolean isSecretValid(Map<String, Object> event) {
        Map<String, String> queryParams = (Map<String, String>) event.get("queryStringParameters");
        String secret = queryParams.getOrDefault(RequestParam.SECRET, "");
        return System.getenv("REGISTRATION_SECRET").equals(secret);
    }

    private Map<String, Object> generatePolicy(String principalId, String effect, String resource) {
        Map<String, Object> authResponse = new HashMap<>();
        authResponse.put("principalId", principalId);
        Map<String, Object> policyDocument = new HashMap<>();
        policyDocument.put("Version", "2012-10-17"); // default version
        Map<String, String> statementOne = new HashMap<>();
        statementOne.put("Action", "execute-api:Invoke"); // default action
        statementOne.put("Effect", effect);
        statementOne.put("Resource", resource);
        policyDocument.put("Statement", new Object[] {statementOne});
        authResponse.put("policyDocument", policyDocument);
        return authResponse;
    }
}
