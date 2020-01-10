exports.handler = function(event, context, callback) {
    if (event.authorizationToken) {

        const token = event.authorizationToken.replace(/Basic /g, '');
        const decoded = Buffer.from(token, 'base64').toString('utf8');
        const credentials = decoded.split(':');

        let policy;
        if (authorizationValid(credentials)) {
            policy = allowPolicy(credentials[0], event.methodArn);
        } else {
            policy = denyPolicy("anonymous", event.methodArn);
        }

        callback(null, policy);

    } else {
        callback(Error("missing authorization token"))
    }
};

const authorizationValid = function(credentials) {
    return 'JUG DA' === credentials[0] && process.env.REGISTRATION_SECRET === credentials[1];
};

const denyPolicy = function(principalId, resource) {
    return generatePolicy(principalId, 'Deny', resource);
};

const allowPolicy = function(principalId, resource) {
    return generatePolicy(principalId, 'Allow', resource);
};

const generatePolicy = function(principalId, effect, resource) {
    const authResponse = {};
    authResponse.principalId = principalId;
    if (effect && resource) {
        const policyDocument = {};
        policyDocument.Version = '2012-10-17'; // default version
        policyDocument.Statement = [];
        const statementOne = {};
        statementOne.Action = 'execute-api:Invoke'; // default action
        statementOne.Effect = effect;
        statementOne.Resource = resource;
        policyDocument.Statement[0] = statementOne;
        authResponse.policyDocument = policyDocument;
    }
    return authResponse;
};
