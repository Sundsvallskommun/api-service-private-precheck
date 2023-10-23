package se.sundsvall.precheck.integration.citizen.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "integration.citizen")
public record CitizenProperties(String tokenUrl, String oauthClientId, String oauthClientSecret, Duration connectTimeout, Duration readTimeout) {}