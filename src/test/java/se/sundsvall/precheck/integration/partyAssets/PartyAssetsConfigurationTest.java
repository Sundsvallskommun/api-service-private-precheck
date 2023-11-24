package se.sundsvall.precheck.integration.partyAssets;

import generated.client.partyAssets.Asset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import se.sundsvall.precheck.api.model.Status;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PartyAssetsConfigurationTest {
    @Mock
    private PartyAssetsClient partyAssetsClient;

    private PartyAssetsIntegration partyAssetsIntegration;
    private UUID uuid;


    @BeforeEach
    void setUp() {
        partyAssetsIntegration = new PartyAssetsIntegration(partyAssetsClient);
        uuid = UUID.randomUUID();
    }

    @Test
    void getCitizen() {
        var asset = ResponseEntity.of(Optional.of(List.of(buildAsset())));
        when(partyAssetsClient.getPartyAssets(any(), any())).thenReturn(asset);

        var response = partyAssetsIntegration.getPartyAssets("someId", Status.ACTIVE);

        assertThat(response).isNotNull().usingRecursiveComparison().isEqualTo(asset);

        verify(partyAssetsClient).getPartyAssets(any(), any());
        verifyNoMoreInteractions(partyAssetsClient);
    }


    @Test
    void getCitizenThrowingException() {
        when(partyAssetsClient.getPartyAssets(any(), any())).thenThrow(new NullPointerException());
        var response = partyAssetsIntegration.getPartyAssets("someId", Status.ACTIVE);

        assertThat(response).isNotNull().usingRecursiveComparison().isEqualTo(ResponseEntity.notFound().build());

        verify(partyAssetsClient).getPartyAssets(any(), any());
        verifyNoMoreInteractions(partyAssetsClient);

    }

    private Asset buildAsset() {
        return new Asset()
                .id(uuid.toString())
                .assetId(uuid.toString())
                .origin("origin")
                .partyId("partyId")
                .caseReferenceIds(List.of("caseReferenceIds"))
                .type("type")
                .issued(LocalDate.now())
                .validTo(LocalDate.now())
                .status(generated.client.partyAssets.Status.ACTIVE)
                .statusReason("statusReason")
                .description("description")
                .additionalParameters(Map.of("key", "value"));

    }
}
