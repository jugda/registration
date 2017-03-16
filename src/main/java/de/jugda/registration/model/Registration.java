package de.jugda.registration.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Data
@DynamoDBTable(tableName = "jugda-registration")
public class Registration {
    @DynamoDBHashKey
    @DynamoDBAttribute
    private String id;
    @DynamoDBAttribute
    private String eventId;
    @DynamoDBAttribute
    private String name;
    @DynamoDBAttribute
    private String email;
    @DynamoDBAttribute
    private Date created;

    public static Registration of(Map<String, String> model) {
        Registration registration = new Registration();
        registration.setId(UUID.randomUUID().toString());
        registration.setEventId(model.get("eventId"));
        registration.setName(model.get("name"));
        registration.setEmail(model.get("email"));
        registration.setCreated(new Date());
        return registration;
    }
}
