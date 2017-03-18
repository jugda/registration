package de.jugda.registration.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
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
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<Registration> registrations = mapper.scan(Registration.class, scanExpression);
        return registrations.stream().filter(r -> eventId.equals(r.getEventId())).collect(Collectors.toList());
    }

    @Override
    public int getRegistrationCount(String eventId) {
        // TODO implement me!
        return 0;
    }

}
