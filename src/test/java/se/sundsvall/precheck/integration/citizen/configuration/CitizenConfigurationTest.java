package se.sundsvall.precheck.integration.citizen.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitizenConfigurationTest {

    @Mock
    private CitizenProperties citizenProperties;

    @Test
    void beanConfigurationAcceptableValuesIsCreated() {
        final String tokenUrl = "mockTokenUrl";
        final String clientId = "mockClientId";
        final String clientSecret = "mockClientSecret";

        when(citizenProperties.tokenUrl()).thenReturn(tokenUrl);
        when(citizenProperties.oauthClientId()).thenReturn(clientId);
        when(citizenProperties.oauthClientSecret()).thenReturn(clientSecret);

        CitizenConfiguration citizenConfiguration = new CitizenConfiguration(citizenProperties);

        var results = citizenConfiguration.feignBuilderCustomizer();
        assertNotNull(results);


        verify(citizenProperties).tokenUrl();
        verify(citizenProperties).oauthClientId();
        verify(citizenProperties).oauthClientSecret();
        verify(citizenProperties).connectTimeout();
        verify(citizenProperties).readTimeout();

        verifyNoMoreInteractions(citizenProperties);
    }
}