package de.jugda.registration.auth;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.extern.log4j.Log4j;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Log4j
public class BasicAuthHandler implements RequestHandler<AuthEvent, Map<String, Object>> {

    @Override
    public Map<String, Object> handleRequest(AuthEvent event, Context context) {
//        log.info("Received Event: " + event);

        String authorizationToken = event.getAuthorizationToken();
        if (null == authorizationToken) {
            throw new RuntimeException("Unauthorized");
        }

        byte[] decoded = Base64.getDecoder().decode(authorizationToken.replace("Basic ", ""));
        String[] credentials = new String(decoded).split(":");

        Map<String, Object> policy;
        if (isAuthorizationValid(credentials[0], credentials[1])) {
            policy = generatePolicy(credentials[0], "Allow", event.getMethodArn());
        } else {
            policy = generatePolicy("anonymous", "Deny", event.getMethodArn());
        }

        return policy;
    }

    private boolean isAuthorizationValid(String username, String password) {
        return "JUG DA".equalsIgnoreCase(username) && System.getenv("REGISTRATION_SECRET").equals(password);
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
