package se.sundsvall.precheck.integration.citizen;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("junit")
@SpringBootTest(classes = CitizenConfiguration.class)
class CitizenConfigurationTest {
    @Autowired
    private FeignBuilderCustomizer citizenCustomizer;

    @Autowired
    private CitizenIntegrationProperties properties;

    @Test
    void testCitizenCustomizer() {
        assertThat(citizenCustomizer).isNotNull();
    }

    @Test
    void propertiesBeanIsCreatedAndHasCorrectValues() {
        assertThat(properties).isNotNull().isNotEqualTo(new CitizenIntegrationProperties());
        assertThat(properties.getBaseUrl()).isEqualTo("http://citizen.url");
        assertThat(properties.getOAuth2()).isNotNull().satisfies(oAuth2 -> {
            assertThat(oAuth2.getTokenUri()).isEqualTo("http://citizen.token");
            assertThat(oAuth2.getClientId()).isEqualTo("citizen-id");
            assertThat(oAuth2.getClientSecret()).isEqualTo("citizen-secret");
            assertThat(oAuth2.getGrantType()).isEqualTo("client_credentials");
        });
        assertThat(properties.getConnectTimeout()).isEqualTo(Duration.ofSeconds(7));
        assertThat(properties.getReadTimeout()).isEqualTo(Duration.ofSeconds(7));
    }
}
