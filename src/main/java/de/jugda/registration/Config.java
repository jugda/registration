package de.jugda.registration;

import io.quarkus.arc.config.ConfigProperties;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@ConfigProperties(prefix = "app")
public class Config {

    public EmailConfig email;
    public EventsConfig events;
    public DynamoDbConfig dynamodb;

    public static class EmailConfig {
        public String from;
        public String subjectPrefix;
    }

    public static class EventsConfig {
        public String jsonUrl;
        public String dataBucket;
        public String dataKey;
    }

    public static class DynamoDbConfig {
        public String table;
        public String index;
    }
}
