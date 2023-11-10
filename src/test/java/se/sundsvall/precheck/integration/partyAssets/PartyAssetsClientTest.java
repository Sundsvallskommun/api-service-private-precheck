package se.sundsvall.precheck.integration.partyAssets;


import generated.client.partyAssets.Asset;
import generated.client.partyAssets.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.zalando.problem.Problem;
import se.sundsvall.precheck.integration.partyAssets.configuration.PartyAssetsIntegration;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartyAssetsClientTest {
    @Mock
    private PartyAssetsClient mockPartyAssetsClient;

    @InjectMocks
    private PartyAssetsIntegration partyAssetsIntegration;

    @Test
    void getPartyAssets_returnOk() {
        when(mockPartyAssetsClient.getPartyAssets(anyString(), anyString())).thenReturn(ResponseEntity.of(Optional.of(List.of(new Asset()))));

        final var result = partyAssetsIntegration.getPartyAssets("someExternalCaseId", String.valueOf(Status.ACTIVE));

        assertThat(result).isEqualTo(ResponseEntity.of(Optional.of(List.of(new Asset()))));

        verify(mockPartyAssetsClient).getPartyAssets(anyString(), anyString());
        verifyNoMoreInteractions(mockPartyAssetsClient);
    }

    @Test
    void getPartyAssets_returnError() {
        when(mockPartyAssetsClient.getPartyAssets(anyString(), anyString())).thenThrow(Problem.builder().build());

        final var result = partyAssetsIntegration.getPartyAssets("someExternalCaseId", String.valueOf(Status.ACTIVE));

        assertThat(result).isEqualTo(null);

        verify(mockPartyAssetsClient).getPartyAssets(anyString(), anyString());
        verifyNoMoreInteractions(mockPartyAssetsClient);
    }
}