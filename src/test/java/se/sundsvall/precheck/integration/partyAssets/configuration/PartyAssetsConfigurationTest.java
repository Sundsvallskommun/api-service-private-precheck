package se.sundsvall.precheck.integration.partyAssets.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartyAssetsConfigurationTest {

    @Mock
    private PartyAssetsProperties mockPartyAssetsProperties;

    @Test
    void beanConfigurationAcceptableValuesIsCreated() {
        final String tokenUrl = "mockTokenUrl";
        final String clientId = "mockClientId";
        final String clientSecret = "mockClientSecret";

        when(mockPartyAssetsProperties.tokenUrl()).thenReturn(tokenUrl);
        when(mockPartyAssetsProperties.oauthClientId()).thenReturn(clientId);
        when(mockPartyAssetsProperties.oauthClientSecret()).thenReturn(clientSecret);

        PartyAssetsConfiguration partyAssetsConfiguration = new PartyAssetsConfiguration(mockPartyAssetsProperties);

        var results = partyAssetsConfiguration.feignBuilderCustomizer();
        assertNotNull(results);
    }
}