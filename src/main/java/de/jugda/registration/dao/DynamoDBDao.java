package de.jugda.registration.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import de.jugda.registration.model.Registration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Synchronized;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DynamoDBDao implements RegistrationDao {

    private static final DynamoDBMapper mapper = DynamoDBManager.mapper();

    private static volatile DynamoDBDao instance;

    @Synchronized
    public static RegistrationDao instance() {
        if (instance == null) {
            instance = new DynamoDBDao();
        }
        return instance;
    }

    @Override
    public Registration find(Registration registration) {
        Condition eventCondition = createCondition(registration.getEventId());
        Condition emailCondition = createCondition(registration.getEmail());

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterConditionEntry("eventId", eventCondition)
            .withFilterConditionEntry("email", emailCondition);

        PaginatedScanList<Registration> scan = mapper.scan(Registration.class, scanExpression);

        return scan.isEmpty() ? null : scan.get(0);
    }

    @Override
    public void save(Registration registration) {
        mapper.save(registration);
    }

    @Override
    public List<Registration> findAll() {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        return mapper.scan(Registration.class, scanExpression).stream()
            .sorted(Comparator.comparing(Registration::getEventId))
            .collect(Collectors.toList());
    }

    @Override
    public List<Registration> findByEventId(String eventId) {
        DynamoDBScanExpression scanExpression = getEventScanExpression(eventId);
        PaginatedScanList<Registration> scan = mapper.scan(Registration.class, scanExpression);
        return scan.stream()
            .sorted(Comparator.comparing(Registration::getCreated))
            .collect(Collectors.toList());
    }

    @Override
    public Registration findByEventIdAndEmail(String eventId, String email) {
        DynamoDBScanExpression scanExpression = getEventScanExpression(eventId);
        scanExpression.addFilterCondition("email", createCondition(email));
        PaginatedScanList<Registration> scan = mapper.scan(Registration.class, scanExpression);
        return scan.get(0);
    }

    @Override
    public int getCount(String eventId) {
        return mapper.count(Registration.class, getEventScanExpression(eventId));
    }

    @Override
    public void delete(String id) {
        Registration reg = mapper.load(Registration.class, id);
        mapper.delete(reg);
    }

    private DynamoDBScanExpression getEventScanExpression(String eventId) {
        Condition condition = createCondition(eventId);
        return new DynamoDBScanExpression()
            .withFilterConditionEntry("eventId", condition);
    }

    private Condition createCondition(String value) {
        return new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue(value));
    }

}
