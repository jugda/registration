package de.jugda.registration;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder;
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import com.amazonaws.services.lambda.runtime.Context;
import de.jugda.registration.service.DynamoDBService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Niko Köbler (for msg systems ag) 2017
 */
@Slf4j
@RunWith(PowerMockRunner.class)
@PrepareForTest(FormHandler.class)
public class FormHandlerTest {

    private FormHandler formHandler = new FormHandler();
    private Context context = new MockLambdaContext();
    private DynamoDBService dynamoDBService;

    @Before
    @SneakyThrows
    public void before() {
        dynamoDBService = mock(DynamoDBService.class);
        when(dynamoDBService.getRegistrationCount(anyString())).thenReturn(0);

        PowerMockito.whenNew(DynamoDBService.class).withNoArguments().thenReturn(dynamoDBService);
    }

    @Test
    public void testFormRequest() {
        AwsProxyRequest request = new AwsProxyRequestBuilder()
            .queryString("eventId", "2017-03-16")
            .queryString("limit", "80")
            .queryString("deadline", "2099-12-31T23:59:59")
            .build();

        AwsProxyResponse response = formHandler.handleRequest(request, context);
        log.info(response.getBody());

        assertTrue(response.getBody().contains("<form method=\"post\">"));
    }

    @Test
    public void testFormRequest_limitSucceeded() {
        when(dynamoDBService.getRegistrationCount(anyString())).thenReturn(80);

        AwsProxyRequest request = new AwsProxyRequestBuilder()
            .queryString("eventId", "2017-03-16")
            .queryString("limit", "80")
            .queryString("deadline", "2099-12-31T23:59:59")
            .build();

        AwsProxyResponse response = formHandler.handleRequest(request, context);
        log.info(response.getBody());

        assertTrue(response.getBody().contains("Für diese Veranstaltung sind alle verfügbaren Plätze bereits belegt."));

    }

    @Test
    public void testFormRequest_closed() {
        AwsProxyRequest request = new AwsProxyRequestBuilder()
            .queryString("eventId", "2017-03-16")
            .queryString("limit", "80")
            .queryString("deadline", "2016-12-31T23:59:59")
            .build();

        AwsProxyResponse response = formHandler.handleRequest(request, context);
        log.info(response.getBody());

        assertTrue(response.getBody().contains("Für diese Veranstaltung ist die Anmeldefrist leider schon abgelaufen."));
    }

}
