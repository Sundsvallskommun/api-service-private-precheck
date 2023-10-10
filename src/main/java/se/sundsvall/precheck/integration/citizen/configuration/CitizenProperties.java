package se.sundsvall.precheck.integration.Citizen.configuration;

import java.time.Duration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "integration.party")
public class CitizenProperties {
    private Duration connectTimeout;
    private Duration readTimeout;

}