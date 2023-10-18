package se.sundsvall.precheck.integration.partyAssets.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "integration.party")
public class PartyAssetsProperties {
    private String tokenUrl;
    private String oauthClientId;
    private String oauthClientSecret;

    private Duration connectTimeout;
    private Duration readTimeout;
}

