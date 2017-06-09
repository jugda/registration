package de.jugda.registration.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import lombok.Synchronized;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
class DynamoDBManager {

    private static volatile DynamoDBManager instance;

    private static DynamoDBMapper mapper;

    private DynamoDBManager() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
            .withPaginationLoadingStrategy(DynamoDBMapperConfig.PaginationLoadingStrategy.EAGER_LOADING)
            .build();
        mapper = new DynamoDBMapper(client, config);
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
