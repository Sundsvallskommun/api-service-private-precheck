package se.sundsvall.precheck.service;

import generated.client.citizen.CitizenAddress;
import generated.client.citizen.CitizenExtended;
import generated.client.partyAssets.Asset;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.precheck.api.model.PreCheckResponse;
import se.sundsvall.precheck.integration.citizen.CitizenClient;
import se.sundsvall.precheck.integration.partyassets.PartyAssetsClient;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.FORBIDDEN;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.precheck.constant.Constants.CORRECT_ADDRESS_TYPE;
import static se.sundsvall.precheck.constant.Constants.NOT_FOUND_ERROR_MESSAGE;
import static se.sundsvall.precheck.constant.Constants.NO_VALID_MUNICIPALITY_ID_FOUND;
/*
 * Unit tests for the PreCheckService class.
 * These tests cover different scenarios related to permit checks and error handling.
 * - The 'testCheckPermitWithEmptyResponses' test checks the behavior when 2xx status codes are received, but the interrogations have no body content.
 * - The 'testIntegrationErrorCases' test covers various integration error cases, including empty responses and forbidden status codes.
 * - The 'testCheckPermitWithResourceNotFound' test verifies the handling of resource not found scenarios.
 *
 */

@ExtendWith(MockitoExtension.class)
class PreCheckServiceTest {
    private static final String TEST_PARTY_ID = "testPartyId";
    private static final String TEST_MUNICIPALITY_ID = "testMunicipalityId";
    private static final String TEST_ASSET_TYPE = "testAssetType";
    private static final String OTHER_ASSET_TYPE = "otherAssetType";
    private static final CitizenExtended VALID_CITIZEN_RESPONSE = new CitizenExtended();
    private static final Asset VALID_ASSET = new Asset();
    @Mock
    private CitizenClient citizenClient;
    @Mock
    private PartyAssetsClient partyAssetsClient;
    @InjectMocks
    private PreCheckService preCheckService;

    private static Stream<Arguments> errorCases() {
        return Stream.of(
                Arguments.of(ResponseEntity.noContent().build(), ResponseEntity.ok().build()),
                Arguments.of(ResponseEntity.ok().build(), ResponseEntity.noContent().build()),
                Arguments.of(ResponseEntity.status(FORBIDDEN.getStatusCode()).build(), ResponseEntity.ok().build()),
                Arguments.of(ResponseEntity.ok().build(), ResponseEntity.status(FORBIDDEN.getStatusCode()).build())
        );
    }

    @BeforeAll
    public static void setUp() {
        CitizenAddress validCitizenAddress = new CitizenAddress();
        validCitizenAddress.setAddressType(CORRECT_ADDRESS_TYPE);
        validCitizenAddress.setMunicipality(TEST_MUNICIPALITY_ID);
        VALID_CITIZEN_RESPONSE.setAddresses(List.of(validCitizenAddress));

        VALID_ASSET.setType(TEST_ASSET_TYPE);
    }


    @Test
    void IntegrationResponses_TestEmpty200IntegrationResponses() {
        when(citizenClient.getCitizen(eq(TEST_PARTY_ID)))
                .thenReturn(ResponseEntity.ok().build());

        when(partyAssetsClient.getPartyAssets(eq(TEST_PARTY_ID), anyString()))
                .thenReturn(ResponseEntity.ok().build());

        Exception exception = assertThrows(
                Exception.class,
                () -> preCheckService.checkPermit(TEST_PARTY_ID, TEST_MUNICIPALITY_ID, TEST_ASSET_TYPE)
        );

        assertEquals(
                Problem.valueOf(NOT_FOUND, String.format(NOT_FOUND_ERROR_MESSAGE, TEST_PARTY_ID)).getMessage(),
                exception.getMessage()
        );
        assertEquals(
                Problem.valueOf(NOT_FOUND).getStatus(),
                ((Problem) exception).getStatus()
        );
    }

    @ParameterizedTest
    @MethodSource("errorCases")
    void IntegrationResponsesTest_OneIntegrationErrorCases(ResponseEntity<CitizenExtended> citizenResponse, ResponseEntity<List<Asset>> assetsResponse) {
        when(citizenClient.getCitizen(eq(TEST_PARTY_ID))).thenReturn(citizenResponse);
        when(partyAssetsClient.getPartyAssets(eq(TEST_PARTY_ID), anyString())).thenReturn(assetsResponse);

        Exception exception = assertThrows(
                Exception.class,
                () -> preCheckService.checkPermit(TEST_PARTY_ID, TEST_MUNICIPALITY_ID, TEST_ASSET_TYPE)
        );

        assertProblemWithNoContent(exception);
    }

    private void assertProblemWithNoContent(Exception e) {
        assertEquals(Problem.valueOf(NOT_FOUND, String.format(NOT_FOUND_ERROR_MESSAGE, TEST_PARTY_ID)).getMessage(), e.getMessage());
        assertEquals(Problem.valueOf(NOT_FOUND).getStatus(), ((Problem) e).getStatus());
    }


    @Test
    void IntegrationResponsesTest_NoContentIntegrationResponse() {
        when(citizenClient.getCitizen(eq(TEST_PARTY_ID)))
                .thenReturn(ResponseEntity.noContent().build());
//
//        when(partyAssetsClient.getPartyAssets(eq(TEST_PARTY_ID), anyString()))
//                .thenReturn(ResponseEntity.noContent().build());

        Exception exception = assertThrows(
                Exception.class,
                () -> preCheckService.checkPermit(TEST_PARTY_ID, TEST_MUNICIPALITY_ID, TEST_ASSET_TYPE)
        );
        assertEquals(
                Problem.valueOf(NOT_FOUND, String.format(NOT_FOUND_ERROR_MESSAGE, TEST_PARTY_ID)).getMessage(),
                exception.getMessage()
        );
        assertEquals(
                Problem.valueOf(NOT_FOUND).getStatus(),
                ((Problem) exception).getStatus()
        );

    }

    @Test
    void IntegrationResponsesTest_AllValidValues() {
        when(citizenClient.getCitizen(eq(TEST_PARTY_ID)))
                .thenReturn(ResponseEntity.ok(VALID_CITIZEN_RESPONSE));

        when(partyAssetsClient.getPartyAssets(eq(TEST_PARTY_ID), anyString()))
                .thenReturn(ResponseEntity.ok(List.of(VALID_ASSET)));

        ResponseEntity<List<PreCheckResponse>> result = preCheckService.checkPermit(TEST_PARTY_ID, TEST_MUNICIPALITY_ID, TEST_ASSET_TYPE);

        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());

        PreCheckResponse responseBody = result.getBody().get(0);

        assertEquals(TEST_ASSET_TYPE, responseBody.getAssetType());
        assertFalse(responseBody.isEligible());
        assertFalse(responseBody.getMessage().isEmpty());

    }

    @Test
    void ResponseTest_WithoutGivenAssetType() {

        when(citizenClient.getCitizen(eq(TEST_PARTY_ID)))
                .thenReturn(ResponseEntity.ok(VALID_CITIZEN_RESPONSE));
        when(partyAssetsClient.getPartyAssets(eq(TEST_PARTY_ID), anyString()))
                .thenReturn(ResponseEntity.ok(List.of(
                        VALID_ASSET,
                        new Asset().type(OTHER_ASSET_TYPE)
                )));

        ResponseEntity<List<PreCheckResponse>> result = preCheckService.checkPermit(TEST_PARTY_ID, TEST_MUNICIPALITY_ID, "");

        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());

        assertEquals(TEST_ASSET_TYPE, result.getBody().get(0).getAssetType());
        assertEquals(OTHER_ASSET_TYPE, result.getBody().get(1).getAssetType());

        for (PreCheckResponse responseBody : result.getBody()) {
            assertFalse(responseBody.isEligible());
            assertEquals("", responseBody.getMessage());
        }
    }

    @Test
    void ResponseTest_InvalidMunicipalityId() {
        CitizenExtended badMunicipalityIdCitizen = new CitizenExtended();
        CitizenAddress badMunicipalityIdAddress = new CitizenAddress();
        badMunicipalityIdAddress.setAddressType(CORRECT_ADDRESS_TYPE);
        badMunicipalityIdAddress.setMunicipality("invalidMunicipalityId");
        badMunicipalityIdCitizen.setAddresses(List.of(badMunicipalityIdAddress));

        when(citizenClient.getCitizen(eq(TEST_PARTY_ID)))
                .thenReturn(ResponseEntity.ok(badMunicipalityIdCitizen));
        when(partyAssetsClient.getPartyAssets(eq(TEST_PARTY_ID), anyString()))
                .thenReturn(ResponseEntity.ok(List.of(VALID_ASSET)));

        Exception exception = assertThrows(
                Exception.class,
                () -> preCheckService.checkPermit(TEST_PARTY_ID, "127", TEST_ASSET_TYPE)
        );

        ThrowableProblem problem = Problem.valueOf(BAD_REQUEST, NO_VALID_MUNICIPALITY_ID_FOUND);

        assertEquals(
                problem.getStatus(),
                ((Problem) exception).getStatus()
        );
        assertEquals(
                problem.getMessage(),
                exception.getMessage()
        );

    }

}