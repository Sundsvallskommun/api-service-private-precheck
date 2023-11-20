package se.sundsvall.precheck.integration.citizen;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.precheck.integration.citizen.configuration.CitizenIntegration;

@ExtendWith(MockitoExtension.class)
class CitizenClientTest {
    private static final String PATH = "/PreCheck/{partyId}";
    @Mock
    private CitizenClient mockCitizenClient;
    @Autowired
    private WebTestClient webTestClient;
    @InjectMocks
    private CitizenIntegration citizenIntegration;

    @Test
    void getCitizen_returnOk() { //TODO FIX THIS!
//        final var response = webTestClient.get().uri(builder -> builder.path(PATH).build("someUUID"))
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(PreCheckResponse.class)
//                .returnResult()
//                .getResponseBody();
//
//        assertThat(response).isNotNull();
//
//    }

//    @Test
//    void getCitizen_returnOk() {
//        final var responsePreCheckResponse = PreCheckResponse.builder()
//                .withAssetType("someAssetType")
//                .withEligible(false)
//                .withMessage("")
//                .build();
//
//        when(mockCitizenClient.getCitizen(anyString()))
//                .thenReturn((ResponseEntity<CitizenExtended>) ResponseEntity.ok().body(responsePreCheckResponse));
//
//        final var result = citizenIntegration.getCitizen("someUUID");
//
//        assertThat(result).isEqualTo(ResponseEntity.ok().body(responsePreCheckResponse));
//
//        verify(mockCitizenClient).getCitizen(anyString());
//        verifyNoMoreInteractions(mockCitizenClient);
//    }

    }
}