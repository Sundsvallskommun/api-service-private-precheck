package se.sundsvall.precheck.integration.partyassets.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "integration.party-assets")
public record PartyAssetsProperties(String tokenUrl, String oauthClientId, String oauthClientSecret,
                                    Duration connectTimeout, Duration readTimeout) {
}
