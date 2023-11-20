package se.sundsvall.precheck.integration.partyassets.configuration;

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
class PartyAssetsPropertiesTest {
    @Autowired
    private PartyAssetsProperties partyAssetsProperties;
    @Value("${integration.party-assets.tokenUrl}")
    private String tokenUrl;

    @Value("${integration.party-assets.oauthClientId}")
    private String oauthClientId;

    @Value("${integration.party-assets.oauthClientSecret}")
    private String oauthClientSecret;

    @Value("${integration.party-assets.connectTimeout}")
    private Duration connectTimeout;

    @Value("${integration.party-assets.readTimeout}")
    private Duration readTimeout;

    @Test
    void testPartyAssetsProperties() {
        assertThat(partyAssetsProperties).isNotNull();
        assertThat(partyAssetsProperties.tokenUrl()).isEqualTo(tokenUrl);
        assertThat(partyAssetsProperties.oauthClientId()).isEqualTo(oauthClientId);
        assertThat(partyAssetsProperties.oauthClientSecret()).isEqualTo(oauthClientSecret);
        assertThat(partyAssetsProperties.connectTimeout()).isEqualTo(connectTimeout);
        assertThat(partyAssetsProperties.readTimeout()).isEqualTo(readTimeout);
    }
}