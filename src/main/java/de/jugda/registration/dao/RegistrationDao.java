package de.jugda.registration.dao;

import de.jugda.registration.model.Registration;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.Select;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@ApplicationScoped
public class RegistrationDao {

    @Inject
    DynamoDbClient dynamoDB;

    private static final String attributesToGet = "id, eventId, #name, email, pub, waitlist, privacy, created, #ttl";
    private static final Map<String, String> expressionAttributeNames;

    static {
        expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put("#name", "name");
        expressionAttributeNames.put("#ttl", "ttl");
    }

    public void save(Registration registration) {
        Map<String, AttributeValue> item = registration.toItem();

        PutItemRequest putItemRequest = PutItemRequest.builder()
            .tableName(getTableName())
            .item(item)
            .build();

        dynamoDB.putItem(putItemRequest);
    }

    public Registration find(Registration registration) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":v_eventId", AttributeValue.builder().s(registration.getEventId()).build());
        expressionAttributeValues.put(":v_email", AttributeValue.builder().s(registration.getEmail()).build());

        ScanRequest scanRequest = createBaseScanRequestBuilder()
            .filterExpression("eventId = :v_eventId and email = :v_email")
            .expressionAttributeValues(expressionAttributeValues)
            .build();

        return dynamoDB.scanPaginator(scanRequest).items().stream()
            .map(Registration::from)
            .findFirst()
            .orElse(null);
    }

    public List<Registration> findAll() {
        return dynamoDB.scanPaginator(createBaseScanRequestBuilder().build())
            .items().stream()
            .map(Registration::from)
            .sorted(Comparator.comparing(Registration::getEventId))
            .collect(Collectors.toList());
    }

    public List<Registration> findByEventId(String eventId) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":v_eventId", AttributeValue.builder().s(eventId).build());

        ScanRequest scanRequest = createBaseScanRequestBuilder()
            .filterExpression("eventId = :v_eventId")
            .expressionAttributeValues(expressionAttributeValues)
            .build();

        return dynamoDB.scanPaginator(scanRequest).items().stream()
            .map(Registration::from)
            .sorted(Comparator.comparing(Registration::getCreated))
            .collect(Collectors.toList());
    }

    public List<Registration> findWaitlistByEventId(String eventId) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":v_eventId", AttributeValue.builder().s(eventId).build());
        expressionAttributeValues.put(":v_waitlist", AttributeValue.builder().bool(true).build());

        ScanRequest scanRequest = createBaseScanRequestBuilder()
            .filterExpression("eventId = :v_eventId and waitlist = :v_waitlist")
            .expressionAttributeValues(expressionAttributeValues)
            .build();

        return dynamoDB.scanPaginator(scanRequest).items().stream()
            .map(Registration::from)
            .sorted(Comparator.comparing(Registration::getCreated))
            .collect(Collectors.toList());
    }

    public int getCount(String eventId) {
        ScanRequest scanRequest = ScanRequest.builder()
            .tableName(getTableName())
            .filterExpression("eventId = :v_eventId")
            .expressionAttributeValues(Collections.singletonMap(":v_eventId", AttributeValue.builder().s(eventId).build()))
            .select(Select.COUNT)
            .build();
        return dynamoDB.scan(scanRequest).count();
    }

    public Registration delete(String id) {
        Map<String, AttributeValue> key = Collections.singletonMap("id", AttributeValue.builder().s(id).build());

        GetItemRequest getItemRequest = GetItemRequest.builder()
            .tableName(getTableName())
            .key(key)
            .projectionExpression(attributesToGet)
            .expressionAttributeNames(expressionAttributeNames)
            .build();

        Registration registration = Registration.from(dynamoDB.getItem(getItemRequest).item());
        //noinspection ResultOfMethodCallIgnored
        registration.getId(); // materialize object

        DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
            .tableName(getTableName())
            .key(key)
            .build();

        dynamoDB.deleteItem(deleteItemRequest);

        return registration;
    }

    private ScanRequest.Builder createBaseScanRequestBuilder() {
        return ScanRequest.builder()
            .tableName(getTableName())
            .projectionExpression(attributesToGet)
            .expressionAttributeNames(expressionAttributeNames)
            ;
    }

    private String getTableName() {
        return System.getenv("DYNAMODB_TABLE");
    }

}
