package se.sundsvall.precheck.integration.partyassets.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.party-assets")
public record PartyAssetsProperties(int connectTimeout, int readTimeout) {
}
