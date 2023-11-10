package se.sundsvall.precheck.service;

import generated.client.citizen.CitizenExtended;
import generated.client.partyAssets.Asset;
import generated.client.partyAssets.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.zalando.problem.Problem;
import se.sundsvall.precheck.integration.citizen.CitizenClient;
import se.sundsvall.precheck.integration.partyAssets.PartyAssetsClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.zalando.problem.Status.FORBIDDEN;
import static org.zalando.problem.Status.NO_CONTENT;

@ExtendWith(MockitoExtension.class)
class PreCheckServiceTest {

    private static final String NO_CONTENT_FOUND = "No content found for the given partyId: %s";
    private static final String INVALID_MUNICIPALITY_ID = "The owner of the partyId '%s' is not registered in the municipality where the permit is requested";
    private static final String partyId = "testPartyId";
    private static final String municipalityId = "testMunicipalityId";
    private static final String assetType = "testAssetType";
    @Mock
    private CitizenClient citizenClient;
    @Mock
    private PartyAssetsClient partyAssetsClient;
    @InjectMocks
    private PreCheckService preCheckService;

    @Test
    void testCheckPermitWhen2xxStatusCodeButNoBody() {
        // Arrange


        Mockito.when(citizenClient.getCitizen(Mockito.eq(partyId)))
                .thenReturn(ResponseEntity.ok().build());

        Mockito.when(partyAssetsClient.getPartyAssets(Mockito.eq(partyId), Mockito.anyString()))
                .thenReturn(ResponseEntity.ok().build());

        // Act
        try {
            preCheckService.checkPermit(partyId, municipalityId, assetType);
        } catch (Exception e) {
            // Assert
            assertEquals(
                    Problem.valueOf(FORBIDDEN, String.format(INVALID_MUNICIPALITY_ID, partyId)).getMessage(),
                    e.getMessage()
            );
            assertEquals(
                    Problem.valueOf(FORBIDDEN).getStatus(),
                    ((Problem) e).getStatus()
            );
        }

        verify(citizenClient, times(1)).getCitizen(partyId);
        verify(partyAssetsClient, times(1)).getPartyAssets(partyId, String.valueOf(Status.ACTIVE));
    }

    @Test
    void testIntegrationErrors() {
        testErrorCase(ResponseEntity.noContent().build(), ResponseEntity.ok().build());
        testErrorCase(ResponseEntity.ok().build(), ResponseEntity.noContent().build());
        testErrorCase(ResponseEntity.status(FORBIDDEN.getStatusCode()).build(), ResponseEntity.ok().build());
        testErrorCase(ResponseEntity.ok().build(), ResponseEntity.status(FORBIDDEN.getStatusCode()).build());

        verify(citizenClient, times(4)).getCitizen(partyId);
        verify(partyAssetsClient, times(4)).getPartyAssets(partyId, String.valueOf(Status.ACTIVE));
    }

    private void testErrorCase(ResponseEntity<CitizenExtended> citizenResponse, ResponseEntity<List<Asset>> assetsResponse) {
        Mockito.when(citizenClient.getCitizen(Mockito.eq(partyId))).thenReturn(citizenResponse);
        Mockito.when(partyAssetsClient.getPartyAssets(Mockito.eq(partyId), Mockito.anyString())).thenReturn(assetsResponse);

        try {
            preCheckService.checkPermit(partyId, municipalityId, assetType);
        } catch (Exception e) {
            assertEquals(Problem.valueOf(NO_CONTENT, String.format(NO_CONTENT_FOUND, partyId)).getMessage(),
                    e.getMessage());
            assertEquals(Problem.valueOf(NO_CONTENT).getStatus(), ((Problem) e).getStatus());
        }
    }

    @Test
    void testCheckPermitWhenResourceNotFound() {
        Mockito.when(citizenClient.getCitizen(Mockito.eq(partyId)))
                .thenReturn(ResponseEntity.noContent().build());

        Mockito.when(partyAssetsClient.getPartyAssets(Mockito.eq(partyId), Mockito.anyString()))
                .thenReturn(ResponseEntity.noContent().build());

        // Act
        try {
            preCheckService.checkPermit(partyId, municipalityId, assetType);
        } catch (Exception e) {
            // Assert
            assertEquals(
                    Problem.valueOf(NO_CONTENT, String.format(NO_CONTENT_FOUND, partyId)).getMessage(),
                    e.getMessage()
            );
            assertEquals(
                    Problem.valueOf(NO_CONTENT).getStatus(),
                    ((Problem) e).getStatus()
            );
        }

        verify(citizenClient, times(1)).getCitizen(partyId);
        verify(partyAssetsClient, times(1)).getPartyAssets(partyId, String.valueOf(Status.ACTIVE));
    }
}