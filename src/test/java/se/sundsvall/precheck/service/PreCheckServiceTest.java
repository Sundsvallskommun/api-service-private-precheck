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
import se.sundsvall.precheck.api.model.Status;
import se.sundsvall.precheck.integration.citizen.CitizenIntegration;
import se.sundsvall.precheck.integration.partyassets.PartyAssetsIntegration;

import java.util.List;
import java.util.stream.Stream;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.BAD_GATEWAY;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.FORBIDDEN;
import static org.zalando.problem.Status.NOT_FOUND;
import static org.zalando.problem.Status.TOO_MANY_REQUESTS;
import static se.sundsvall.precheck.constant.Constants.CORRECT_ADDRESS_TYPE;
import static se.sundsvall.precheck.constant.Constants.NOT_FOUND_ERROR_MESSAGE;
import static se.sundsvall.precheck.constant.Constants.NO_VALID_MUNICIPALITY_ID_FOUND;

/**
 * Test methods:
 * - IntegrationResponsesTest_AllValidValues(): Tests scenarios with all valid values.
 * - ResponseTest_WithoutGivenAssetType(): Tests response scenarios without a given asset type.
 * - IntegrationResponses_TestEmpty200IntegrationResponses(): Tests behavior with empty 200 integration responses.
 * - IntegrationResponsesTest_OneIntegrationErrorCases(): Parameterized test for one integration error cases.
 * - ResponseTest_InvalidMunicipalityId(): Tests scenarios with an invalid municipality ID.
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
    private CitizenIntegration citizenIntegration;
    @Mock
    private PartyAssetsIntegration partyAssetIntegration;
    @InjectMocks
    private PreCheckService preCheckService;

    private static Stream<Arguments> errorCases() {
        return Stream.of(
                // One response is an error response
                Arguments.of(ResponseEntity.noContent().build(), ResponseEntity.ok().build()),
                Arguments.of(ResponseEntity.ok().build(), ResponseEntity.noContent().build()),
                Arguments.of(ResponseEntity.status(FORBIDDEN.getStatusCode()).build(), ResponseEntity.ok().build()),
                Arguments.of(ResponseEntity.ok().build(), ResponseEntity.status(FORBIDDEN.getStatusCode()).build()),
                Arguments.of(ResponseEntity.status(NOT_FOUND.getStatusCode()).build(), ResponseEntity.ok().build()),
                Arguments.of(ResponseEntity.ok().build(), ResponseEntity.status(NOT_FOUND.getStatusCode()).build()),
                Arguments.of(ResponseEntity.status(BAD_REQUEST.getStatusCode()).build(), ResponseEntity.ok().build()),
                Arguments.of(ResponseEntity.ok().build(), ResponseEntity.status(BAD_REQUEST.getStatusCode()).build()),
                Arguments.of(ResponseEntity.status(TOO_MANY_REQUESTS.getStatusCode()).build(), ResponseEntity.ok().build()),
                Arguments.of(ResponseEntity.ok().build(), ResponseEntity.status(TOO_MANY_REQUESTS.getStatusCode()).build()),
                Arguments.of(ResponseEntity.status(BAD_GATEWAY.getStatusCode()).build(), ResponseEntity.ok().build()),
                Arguments.of(ResponseEntity.ok().build(), ResponseEntity.status(BAD_GATEWAY.getStatusCode()).build()),

                // Both responses are error responses
                Arguments.of(ResponseEntity.status(BAD_REQUEST.getStatusCode()).build(), ResponseEntity.status(BAD_REQUEST.getStatusCode()).build()),
                Arguments.of(ResponseEntity.status(FORBIDDEN.getStatusCode()).build(), ResponseEntity.status(FORBIDDEN.getStatusCode()).build()),
                Arguments.of(ResponseEntity.status(NOT_FOUND.getStatusCode()).build(), ResponseEntity.status(NOT_FOUND.getStatusCode()).build()),
                Arguments.of(ResponseEntity.status(TOO_MANY_REQUESTS.getStatusCode()).build(), ResponseEntity.status(TOO_MANY_REQUESTS.getStatusCode()).build()),
                Arguments.of(ResponseEntity.status(BAD_GATEWAY.getStatusCode()).build(), ResponseEntity.status(BAD_GATEWAY.getStatusCode()).build())
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
    void IntegrationResponsesTest_AllValidValues() {
        when(citizenIntegration.getCitizen(TEST_PARTY_ID))
                .thenReturn(ResponseEntity.ok(VALID_CITIZEN_RESPONSE));

        when(partyAssetIntegration.getPartyAssets(TEST_PARTY_ID, Status.ACTIVE))
                .thenReturn(ResponseEntity.ok(List.of(VALID_ASSET)));

        List<PreCheckResponse> result = preCheckService.checkPermit(TEST_PARTY_ID, TEST_MUNICIPALITY_ID, TEST_ASSET_TYPE);

        assertNotNull(result);
        assertEquals(1, result.size());

        PreCheckResponse responseBody = result.get(0);

        assertEquals(TEST_ASSET_TYPE, responseBody.getAssetType());
        assertFalse(responseBody.isEligible());
        assertFalse(responseBody.getMessage().isEmpty());

    }

    @Test
    void ResponseTest_WithoutGivenAssetType() {

        when(citizenIntegration.getCitizen(TEST_PARTY_ID))
                .thenReturn(ResponseEntity.ok(VALID_CITIZEN_RESPONSE));
        when(partyAssetIntegration.getPartyAssets(TEST_PARTY_ID, Status.ACTIVE))
                .thenReturn(ResponseEntity.ok(List.of(
                        VALID_ASSET,
                        new Asset().type(OTHER_ASSET_TYPE)
                )));

        List<PreCheckResponse> result = preCheckService.checkPermit(TEST_PARTY_ID, TEST_MUNICIPALITY_ID, "");

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(TEST_ASSET_TYPE, result.get(0).getAssetType());
        assertEquals(OTHER_ASSET_TYPE, result.get(1).getAssetType());

        for (var responseBody : result) {
            assertFalse(responseBody.isEligible());
            assertEquals("", responseBody.getMessage());
        }
    }

    @Test
    void IntegrationResponses_TestEmpty200IntegrationResponses() {
        when(citizenIntegration.getCitizen(TEST_PARTY_ID))
                .thenReturn(ResponseEntity.ok().build());

        when(partyAssetIntegration.getPartyAssets(TEST_PARTY_ID, Status.ACTIVE))
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
        when(citizenIntegration.getCitizen(TEST_PARTY_ID)).thenReturn(citizenResponse);
        when(partyAssetIntegration.getPartyAssets(TEST_PARTY_ID, Status.ACTIVE)).thenReturn(assetsResponse);

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
    void ResponseTest_InvalidMunicipalityId() {
        CitizenExtended badMunicipalityIdCitizen = new CitizenExtended();
        CitizenAddress badMunicipalityIdAddress = new CitizenAddress();
        badMunicipalityIdAddress.setAddressType(CORRECT_ADDRESS_TYPE);
        badMunicipalityIdAddress.setMunicipality("invalidMunicipalityId");
        badMunicipalityIdCitizen.setAddresses(List.of(badMunicipalityIdAddress));

        when(citizenIntegration.getCitizen(TEST_PARTY_ID))
                .thenReturn(ResponseEntity.ok(badMunicipalityIdCitizen));
        when(partyAssetIntegration.getPartyAssets(TEST_PARTY_ID, Status.ACTIVE))
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