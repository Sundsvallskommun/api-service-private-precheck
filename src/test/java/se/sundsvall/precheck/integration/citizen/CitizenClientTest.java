package se.sundsvall.precheck.integration.citizen;

import generated.client.citizen.CitizenAddress;
import generated.client.citizen.CitizenExtended;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.zalando.problem.Problem;
import se.sundsvall.precheck.integration.citizen.configuration.CitizenIntegration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitizenClientTest {

    @Mock
    private CitizenClient mockCitizenClient;

    @InjectMocks
    private CitizenIntegration citizenIntegration;

    @Test
    void getCitizen_returnOk() {
        var citizenExtended = new CitizenExtended();

        citizenExtended.setAddresses(List.of(new CitizenAddress()));


        when(mockCitizenClient.getCitizen(anyString())).thenReturn(ResponseEntity.ofNullable(citizenExtended));

        final var results = citizenIntegration.getCitizen("someExternalCaseId");

        assertThat(results).isEqualTo(ResponseEntity.ofNullable(citizenExtended));

        verify(mockCitizenClient).getCitizen(any(String.class));
        verifyNoMoreInteractions(mockCitizenClient);
    }

    @Test
    void getCitizen_returnError() {
        when(mockCitizenClient.getCitizen(anyString()))
                .thenThrow(Problem.builder().build());

        final var result = citizenIntegration.getCitizen("someGUID");

        assertThat(result).isEqualTo(null);

        verify(mockCitizenClient).getCitizen(any(String.class));
        verifyNoMoreInteractions(mockCitizenClient);
    }

}