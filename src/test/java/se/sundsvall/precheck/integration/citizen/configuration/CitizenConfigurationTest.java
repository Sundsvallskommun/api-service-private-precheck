package se.sundsvall.precheck.integration.citizen.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CitizenConfigurationTest {

    @Mock
    private CitizenProperties citizenProperties;
    @InjectMocks
    private CitizenConfiguration citizenConfiguration;

    @Test
    void beanConfiguration_AcceptableValuesIsCreated() {

        final String TOKEN_URL = "mockTokenUrl";
        final String CLIENT_ID = "mockClientId";
        final String CLIENT_SECRET = "mockClientSecret";

        when(citizenProperties.tokenUrl()).thenReturn(TOKEN_URL);
        when(citizenProperties.oauthClientId()).thenReturn(CLIENT_ID);
        when(citizenProperties.oauthClientSecret()).thenReturn(CLIENT_SECRET);

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