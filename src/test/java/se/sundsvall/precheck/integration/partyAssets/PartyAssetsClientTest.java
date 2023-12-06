package se.sundsvall.precheck.integration.partyAssets;

import generated.client.partyAssets.Asset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import se.sundsvall.precheck.api.model.Status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartyAssetsClientTest {
    private final UUID uuid = UUID.randomUUID();
    @Mock
    private PartyAssetsIntegration mockPartyAssetsIntegration;

    @Test
    void testGetAssetEntity_ReturnAsset_whenOk() {
        List<Asset> test_Asset = List.of(new Asset());
        when(mockPartyAssetsIntegration.getPartyAssets(any(String.class), any(Status.class))).thenReturn(ResponseEntity.of(Optional.of(test_Asset)));

        var response = mockPartyAssetsIntegration.getPartyAssets(String.valueOf(uuid), Status.ACTIVE);

        assertThat(response).isNotNull().usingRecursiveComparison().isEqualTo(ResponseEntity.of(Optional.of(test_Asset)));
        verify(mockPartyAssetsIntegration).getPartyAssets(String.valueOf(uuid), Status.ACTIVE);
        verifyNoMoreInteractions(mockPartyAssetsIntegration);
    }

    @Test
    void testGetAssetEntity_ReturnEmpty404_whenNotFound() {
        when(mockPartyAssetsIntegration.getPartyAssets(any(String.class), any(Status.class))).thenReturn(ResponseEntity.notFound().build());

        var response = mockPartyAssetsIntegration.getPartyAssets(String.valueOf(uuid), Status.ACTIVE);

        assertThat(response).isNotNull().usingRecursiveComparison().isEqualTo(ResponseEntity.notFound().build());
        verify(mockPartyAssetsIntegration).getPartyAssets(String.valueOf(uuid), Status.ACTIVE);
        verifyNoMoreInteractions(mockPartyAssetsIntegration);
    }


}