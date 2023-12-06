package se.sundsvall.precheck.integration.partyAssets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("junit")
@SpringBootTest(classes = PartyAssetsConfiguration.class)
public class PartyAssetsConfigurationTest {
    @Autowired
    private FeignBuilderCustomizer partyAssetsCustomizer;

    @Autowired
    private PartyAssetsProperties properties;

    @Test
    void testPartyAssetsCustomizer() {
        assertThat(partyAssetsCustomizer).isNotNull();
    }

    @Test
    void propertiesBeanIsCreatedAndHasCorrectValues() {
        assertThat(properties).isNotNull().isNotEqualTo(new PartyAssetsProperties());
        assertThat(properties.getBaseUrl()).isEqualTo("http://party-assets.url");
        assertThat(properties.getOAuth2()).isNotNull().satisfies(oAuth2 -> {
            assertThat(oAuth2.getTokenUri()).isEqualTo("http://party-assets.token");
            assertThat(oAuth2.getClientId()).isEqualTo("party-assets-id");
            assertThat(oAuth2.getClientSecret()).isEqualTo("party-assets-secret");
            assertThat(oAuth2.getGrantType()).isEqualTo("client_credentials");
        });
        assertThat(properties.getConnectTimeout()).isEqualTo(Duration.ofSeconds(7));
        assertThat(properties.getReadTimeout()).isEqualTo(Duration.ofSeconds(7));
    }
}
