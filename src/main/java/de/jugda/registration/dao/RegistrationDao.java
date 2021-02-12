package de.jugda.registration.dao;

import de.jugda.registration.Config;
import de.jugda.registration.model.Registration;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.Select;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Comparator;
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
    @Inject
    Config config;

    private static final String attributesToGet = "id, eventId, #name, email, pub, waitlist, privacy, created, #ttl";
    private static final Map<String, String> expressionAttributeNames = Map.of("#name", "name", "#ttl", "ttl");

    public void save(Registration registration) {
        dynamoDB.putItem(builder -> builder
            .tableName(config.dynamodb.table)
            .item(registration.toItem()));
    }

    public Registration find(Registration registration) {
        return dynamoDB.query(builder -> baseQueryRequestBuilder(builder)
            .expressionAttributeValues(Map.of(
                ":v_eventId", toAttribute(registration.getEventId()),
                ":v_email", toAttribute(registration.getEmail())
            ))
            .keyConditionExpression("eventId = :v_eventId and email = :v_email")
        ).items().stream()
            .map(Registration::from)
            .findFirst()
            .orElse(null);
    }

    public List<Registration> findAll() {
        return dynamoDB.scanPaginator(builder -> builder
            .tableName(config.dynamodb.table)
            .projectionExpression(attributesToGet)
            .expressionAttributeNames(expressionAttributeNames)
        ).items().stream()
            .map(Registration::from)
            .sorted(Comparator.comparing(Registration::getEventId))
            .collect(Collectors.toList());
    }

    public List<Registration> findByEventId(String eventId) {
        return dynamoDB.queryPaginator(builder -> byEventIdQueryBuilder(builder, eventId))
            .items().stream()
            .map(Registration::from)
            .sorted(Comparator.comparing(Registration::getCreated))
            .collect(Collectors.toList());
    }

    public List<Registration> findWaitlistByEventId(String eventId) {
        return findByEventId(eventId).stream().filter(Registration::isWaitlist).collect(Collectors.toUnmodifiableList());
    }

    public int getCount(String eventId) {
        return dynamoDB.query(builder -> byEventIdQueryBuilder(builder, eventId).projectionExpression(null).expressionAttributeNames(null).select(Select.COUNT)).count();
    }

    public Registration delete(String id) {
        Map<String, AttributeValue> key = Map.of("id", toAttribute(id));

        Registration registration = Registration.from(dynamoDB.getItem(builder -> builder
            .tableName(config.dynamodb.table)
            .key(key)
            .projectionExpression(attributesToGet)
            .expressionAttributeNames(expressionAttributeNames)
        ).item());
        //noinspection ResultOfMethodCallIgnored
        registration.getId(); // materialize object

        dynamoDB.deleteItem(builder -> builder.tableName(config.dynamodb.table).key(key));

        return registration;
    }

    private QueryRequest.Builder baseQueryRequestBuilder(QueryRequest.Builder builder) {
        return builder
            .tableName(config.dynamodb.table)
            .indexName(config.dynamodb.index)
            .projectionExpression(attributesToGet)
            .expressionAttributeNames(expressionAttributeNames)
            ;
    }

    private QueryRequest.Builder byEventIdQueryBuilder(QueryRequest.Builder builder, String eventId) {
        return baseQueryRequestBuilder(builder)
            .keyConditionExpression("eventId = :v_eventId")
            .expressionAttributeValues(Map.of(":v_eventId", toAttribute(eventId)))
            ;
    }

    private AttributeValue toAttribute(String value) {
        return AttributeValue.builder().s(value).build();
    }

}
