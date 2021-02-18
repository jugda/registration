package de.jugda.registration;

import io.quarkus.arc.config.ConfigProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@ConfigProperties(prefix = "app")
public class Config {

    public TenantConfig tenant;

    public EmailConfig email;
    public EventsConfig events;
    public DynamoDbConfig dynamodb;

    public PageConfig page;

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

    @Getter
    @Setter
    @RegisterForReflection
    public static class TenantConfig {
        public String id;
        public String name;
        public String baseUrl;
        public String privacy;
        public String imprint;
        public String logo;
        public String website;
    }

    public static class PageConfig {
        public RegistrationPageConfig registration;
    }

    @Getter
    @Setter
    @RegisterForReflection
    public static class RegistrationPageConfig {
        public String name;
        public String email;
        public String video;
        public String disclaimer;
        public String waitlist;
    }
}
