package de.jugda.registration.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.Synchronized;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
class DynamoDBManager {

    private static volatile DynamoDBManager instance;

    private static DynamoDBMapper mapper;

    private DynamoDBManager() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        mapper = new DynamoDBMapper(client);
    }

    @Synchronized
    private static DynamoDBManager instance() {
        if (instance == null) {
            instance = new DynamoDBManager();
        }
        return instance;
    }

    static DynamoDBMapper mapper() {
        DynamoDBManager manager = instance();
        return manager.mapper;
    }
}
