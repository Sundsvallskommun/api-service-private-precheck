package se.sundsvall.precheck.integration.partyAssets;


import generated.client.partyAssets.Asset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.zalando.problem.Problem;
import se.sundsvall.precheck.api.model.Status;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartyAssetsIntegrationTest {
    @Mock
    private PartyAssetsClient mockPartyAssetsClient;

    @InjectMocks
    private PartyAssetsIntegration mockPartyAssetsIntegration;

    @Test
    void getPartyAssets_ok() {

        final ResponseEntity<List<Asset>> Test_responseEntity = ResponseEntity.of(Optional.of(List.of(new Asset())));

        when(mockPartyAssetsClient.getPartyAssets(anyString(), anyString())).thenReturn(Test_responseEntity);

        final var result = mockPartyAssetsIntegration.getPartyAssets("someGUID", Status.ACTIVE);

        assertThat(result).isEqualTo(Test_responseEntity);

        verify(mockPartyAssetsClient).getPartyAssets(anyString(), anyString());
        verifyNoMoreInteractions(mockPartyAssetsClient);
    }

    @Test
    void getPartyAssetsThrowError() {
        when(mockPartyAssetsClient.getPartyAssets(anyString(), anyString())).thenThrow(Problem.builder().build());

        final var result = mockPartyAssetsIntegration.getPartyAssets("someGUID", Status.ACTIVE);

        assertThat(result).isEqualTo(ResponseEntity.notFound().build());

        verify(mockPartyAssetsClient).getPartyAssets(anyString(), anyString());
        verifyNoMoreInteractions(mockPartyAssetsClient);
    }
}
