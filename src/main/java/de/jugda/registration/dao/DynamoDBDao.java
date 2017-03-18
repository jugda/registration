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

import java.util.List;
import java.util.Map;
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
    public void saveRegistration(Map<String, String> model) {
        Registration registration = Registration.of(model);
        mapper.save(registration);
    }

    @Override
    public List<Registration> getRegistrations(String eventId) {
        Condition condition = new Condition()
            .withComparisonOperator(ComparisonOperator.EQ)
            .withAttributeValueList(new AttributeValue(eventId));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterConditionEntry("eventId", condition);

        PaginatedScanList<Registration> scan = mapper.scan(Registration.class, scanExpression);

        return scan.stream().collect(Collectors.toList());
    }

    @Override
    public int getRegistrationCount(String eventId) {
        return getRegistrations(eventId).size();
    }

}
