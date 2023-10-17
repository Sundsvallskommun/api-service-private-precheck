package se.sundsvall.precheck.integration.partyAssets.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("integration.party")
public record PartyAssetsProperties(int connectTimeout, int readTimeout) {}
