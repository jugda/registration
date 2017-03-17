package de.jugda.registration.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import de.jugda.registration.model.Registration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
public class DynamoDBService {

    private final DynamoDBMapper mapper;

    public DynamoDBService() {
        AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.defaultClient();
        mapper = new DynamoDBMapper(amazonDynamoDB);
    }

    public void saveRegistration(Map<String, String> model) {
        Registration registration = Registration.of(model);
        mapper.save(registration);
    }

    public List<Registration> getRegistrations(String eventId) {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<Registration> registrations = mapper.scan(Registration.class, scanExpression);
        return registrations.stream().filter(r -> eventId.equals(r.getEventId())).collect(Collectors.toList());
    }

    public int getRegistrationCount(String eventId) {
        // TODO implement me!
        return 0;
    }

}
