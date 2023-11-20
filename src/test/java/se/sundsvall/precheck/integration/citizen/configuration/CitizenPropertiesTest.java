package se.sundsvall.precheck.integration.citizen.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.precheck.Application;

import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class CitizenPropertiesTest {
    @Autowired
    private CitizenProperties citizenProperties;
    @Value("${integration.citizen.tokenUrl}")
    private String tokenUrl;

    @Value("${integration.citizen.oauthClientId}")
    private String oauthClientId;

    @Value("${integration.citizen.oauthClientSecret}")
    private String oauthClientSecret;

    @Value("${integration.citizen.connectTimeout}")
    private Duration connectTimeout;

    @Value("${integration.citizen.readTimeout}")
    private Duration readTimeout;

    @Test
    void testCitizenProperties() {
        assertThat(citizenProperties).isNotNull();
        assertThat(citizenProperties.tokenUrl()).isEqualTo(tokenUrl);
        assertThat(citizenProperties.oauthClientId()).isEqualTo(oauthClientId);
        assertThat(citizenProperties.oauthClientSecret()).isEqualTo(oauthClientSecret);
        assertThat(citizenProperties.connectTimeout()).isEqualTo(connectTimeout);
        assertThat(citizenProperties.readTimeout()).isEqualTo(readTimeout);
    }
}