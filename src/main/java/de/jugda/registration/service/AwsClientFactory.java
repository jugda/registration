package de.jugda.registration.service;

import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

import javax.enterprise.inject.Produces;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
public class AwsClientFactory {

    @Produces
    public SesClient sesClient() {
        return SesClient.builder()
            .region(Region.of(System.getenv("SES_REGION")))
            .httpClientBuilder(UrlConnectionHttpClient.builder())
            .build();
    }

}
