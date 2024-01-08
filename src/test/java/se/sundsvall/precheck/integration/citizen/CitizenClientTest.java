package se.sundsvall.precheck.integration.citizen;

import generated.client.citizen.CitizenExtended;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CitizenClientTest {
    private final UUID uuid = UUID.randomUUID();
    @Mock
    private CitizenIntegration mockCitizenIntegration;

    @Test
    void testGetCitizenEntity_ReturnCitizenExtended_whenOk() {
        CitizenExtended test_citizen = new CitizenExtended();

        when(mockCitizenIntegration.getCitizen(any(String.class))).thenReturn(ResponseEntity.of(Optional.of(test_citizen)));

        var response = mockCitizenIntegration.getCitizen(String.valueOf(uuid));

        assertThat(response).isNotNull().usingRecursiveComparison().isEqualTo(ResponseEntity.of(Optional.of(test_citizen)));
        verify(mockCitizenIntegration).getCitizen(String.valueOf(uuid));
        verifyNoMoreInteractions(mockCitizenIntegration);
    }

    @Test
    void testGetCitizenEntity_ReturnEmpty404_whenNotFound() {
        when(mockCitizenIntegration.getCitizen(any(String.class))).thenReturn(ResponseEntity.notFound().build());

        var response = mockCitizenIntegration.getCitizen(String.valueOf(uuid));

        assertThat(response).isNotNull().usingRecursiveComparison().isEqualTo(ResponseEntity.notFound().build());
        verify(mockCitizenIntegration).getCitizen(String.valueOf(uuid));
        verifyNoMoreInteractions(mockCitizenIntegration);
    }
}