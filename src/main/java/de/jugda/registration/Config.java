package de.jugda.registration;

import io.smallrye.config.ConfigMapping;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@ConfigMapping(prefix = "app")
public interface Config {

    TenantConfig tenant();

    EmailConfig email();
    EventsConfig events();
    DynamoDbConfig dynamodb();

    PageConfig page();

    interface EmailConfig {
        String from();
        String subjectPrefix();
    }

    interface EventsConfig {
        String jsonUrl();
        String dataBucket();
        String dataKey();
    }

    interface DynamoDbConfig {
        String table();
        String index();
    }

    interface TenantConfig {
        String id();
        String name();
        String baseUrl();
        String privacy();
        String imprint();
        String logo();
        String website();
        String youtube();
    }

    interface PageConfig {
        RegistrationPageConfig registration();
        WebinarPageConfig webinar();
    }

    interface RegistrationPageConfig {
        String name();
        String email();
        String video();
        String disclaimer();
        String waitlist();
    }

    interface WebinarPageConfig {
        String tools();
        String communication();
        String recording();
        String recordingPrivacyHint();
    }
}
