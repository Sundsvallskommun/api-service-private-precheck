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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CitizenIntegrationTest {
    @Mock
    private CitizenClient mockCitizenClient;

    @InjectMocks
    private CitizenIntegration mockCitizenIntegration;

    @Test
    void getPartyAssets_ok() {

        final ResponseEntity<CitizenExtended> Test_responseEntity = ResponseEntity.of(java.util.Optional.of(buildCitizen()));

        when(mockCitizenClient.getCitizen(anyString())).thenReturn(Test_responseEntity);

        final var result = mockCitizenIntegration.getCitizen("someGUID");

        assertThat(result).isEqualTo(Test_responseEntity);

        verify(mockCitizenClient).getCitizen(anyString());
        verifyNoMoreInteractions(mockCitizenClient);
    }

    @Test
    void getPersonID_error() {
        when(mockCitizenClient.getCitizen(anyString())).thenThrow(Problem.builder().build());

        final var result = mockCitizenIntegration.getCitizen("someGUID");

        assertThat(result).isEqualTo(ResponseEntity.notFound().build());

        verify(mockCitizenClient).getCitizen(anyString());
        verifyNoMoreInteractions(mockCitizenClient);
    }

    private CitizenExtended buildCitizen() {
        return new CitizenExtended()
                .givenname("someGivenName")
                .lastname("someLastName")
                .personId(UUID.randomUUID())
                .nrDate("someNrDate")
                .addresses(List.of(
                        new CitizenAddress()
                                .status("CURRENT")
                                .address("someAdress")
                                .addressNumber("someAdressNumber")
                                .addressArea("someAdressArea")
                                .co("someCo")
                                .postalCode("somePostalCode")
                                .city("someCity")
                                .country("someCountry")));

    }
}