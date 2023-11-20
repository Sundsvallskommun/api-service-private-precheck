package se.sundsvall.precheck.integration.partyassets.configuration;

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
        final String TOKEN_URL = "mockTokenUrl";
        final String CLIENT_ID = "mockClientId";
        final String CLIENT_SECRET = "mockClientSecret";

        when(mockPartyAssetsProperties.tokenUrl()).thenReturn(TOKEN_URL);
        when(mockPartyAssetsProperties.oauthClientId()).thenReturn(CLIENT_ID);
        when(mockPartyAssetsProperties.oauthClientSecret()).thenReturn(CLIENT_SECRET);

        PartyAssetsConfiguration partyAssetsConfiguration = new PartyAssetsConfiguration(mockPartyAssetsProperties);

        var results = partyAssetsConfiguration.feignBuilderCustomizer();
        assertNotNull(results);
    }
}