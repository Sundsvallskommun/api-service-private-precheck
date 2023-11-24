package se.sundsvall.precheck.integration.citizen;

import generated.client.citizen.CitizenAddress;
import generated.client.citizen.CitizenExtended;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CitizenConfigurationTest {
    @Mock
    private CitizenClient mockCitizenClient;

    private CitizenIntegration mockCitizenIntegration;
    private UUID uuid;


    @BeforeEach
    void setUp() {
        mockCitizenIntegration = new CitizenIntegration(mockCitizenClient);
        uuid = UUID.randomUUID();
    }

    @Test
    void getCitizen() {
        final var test_citizen = ResponseEntity.of(Optional.of(buildCitizen()));
        when(mockCitizenClient.getCitizen(any())).thenReturn(test_citizen);

        var response = mockCitizenIntegration.getCitizen("someId");

        assertThat(response).isNotNull().usingRecursiveComparison().isEqualTo(test_citizen);

        verify(mockCitizenClient).getCitizen(any());
        verifyNoMoreInteractions(mockCitizenClient);
    }


    @Test
    void getCitizenThrowingException() {
        when(mockCitizenClient.getCitizen(any())).thenThrow(new NullPointerException());
        var response = mockCitizenIntegration.getCitizen("someId");

        assertThat(response).isNotNull().usingRecursiveComparison().isEqualTo(ResponseEntity.notFound().build());

        verify(mockCitizenClient).getCitizen(any());
        verifyNoMoreInteractions(mockCitizenClient);

    }

    private CitizenExtended buildCitizen() {
        return new CitizenExtended()
                .givenname("someGivenName")
                .lastname("someLastName")
                .personId(uuid)
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
