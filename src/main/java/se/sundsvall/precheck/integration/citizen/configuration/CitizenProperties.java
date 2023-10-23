package se.sundsvall.precheck.integration.citizen.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "integration.citizen")
public class CitizenProperties { //TODO Remove the lazy self-made record and use the one from Lombok
    private String tokenUrl;
    private String oauthClientId;
    private String oauthClientSecret;

    private Duration connectTimeout;
    private Duration readTimeout;
}
