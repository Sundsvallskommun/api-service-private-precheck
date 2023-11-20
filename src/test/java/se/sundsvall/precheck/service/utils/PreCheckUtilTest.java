package se.sundsvall.precheck.service.utils;

import generated.client.citizen.CitizenAddress;
import generated.client.citizen.CitizenExtended;
import generated.client.partyAssets.Asset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import se.sundsvall.precheck.api.model.PreCheckResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.precheck.service.utils.PreCheckUtil.checkResourceAvailability;
import static se.sundsvall.precheck.service.utils.PreCheckUtil.containsValidMunicipalityId;
import static se.sundsvall.precheck.service.utils.PreCheckUtil.createPrecheckResponse;
import static se.sundsvall.precheck.service.utils.PreCheckUtil.generateAssetTypeResponses;
import static se.sundsvall.precheck.service.utils.PreCheckUtil.generateNoAssetTypeResponses;

/**
 * Test methods:
 * - resourceNotFound_RerunsFalse_InValid(): Tests the behavior when resource availability is invalid.
 * - resourceNotFound_CitizenError_ReturnsTrue(): Parameterized test for citizen error scenarios.
 * - has_InValid_Municipality_Id_Return_Valid_Id_Response(): Tests the validation of municipality ID.
 * - generateAssetTypeResponses_returnResponseOk(): Parameterized test for handling different asset types with valid responses.
 * - generateAssetTypeResponses_returnResponseOk_whenEmpty(): Tests the handling of asset types with an empty response.
 * - generateAssetTypeResponses_returnResponseOk_whenNull(): Tests the handling of asset types with a null response.
 * - generateAssetTypeResponses_returnResponseOk_whenAssetTypeExists(): Tests the handling of asset types when the type exists.
 * - handleNoGivenAssetType_ReturnListOfAssets(): Tests the handling of scenarios where no asset type is given.
 * - handleNoGivenAssetType_Null_ReturnEmpty(): Tests the handling when no asset type is given (null response).
 * - handleNoGivenAssetType_Empty_ReturnEmpty(): Tests the handling when no asset type is given (empty response).
 * - buildPreCheckResponse_ReturnValidPreCheckResponse(): Tests the creation of a valid PreCheckResponse.
 */


class PreCheckUtilTest {
    private static final String WANTED_ASSET_TYPE_NAME = "WantedAssetTypeName";
    private final ResponseEntity<CitizenExtended> MOCK_CITIZEN = mock(ResponseEntity.class);
    private final ResponseEntity<List<Asset>> MOCK_PARTY = mock(ResponseEntity.class);
    private final Asset MOCK_ASSET = mock(Asset.class);
    private final ResponseEntity<Object> REQUEST_OK = ResponseEntity.ok().build();

    private static Stream<Arguments> getAssetTypeValues() {
        return Stream.of(
                Arguments.of("FirstRandomAssetType"),
                Arguments.of("SecondRandomAssetType"),
                Arguments.of(WANTED_ASSET_TYPE_NAME)
        );
    }

    private static Stream<Arguments> getCitizenErrorValues() {
        return Stream.of(
                Arguments.of(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()),
                Arguments.of(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()),
                Arguments.of(ResponseEntity.status(HttpStatus.NO_CONTENT).build()),
                Arguments.of(ResponseEntity.status(HttpStatus.FORBIDDEN).build())
        );
    }

    @Test
    void resourceNotFound_RerunsFalse_InValid() {
        when(MOCK_CITIZEN.getStatusCode()).thenReturn(HttpStatus.OK);
        when(MOCK_PARTY.getStatusCode()).thenReturn(HttpStatus.OK);

        boolean result = checkResourceAvailability(MOCK_CITIZEN, MOCK_PARTY);

        assertTrue(result);

        verify(MOCK_CITIZEN, times(2)).getStatusCode();
        verify(MOCK_PARTY, times(2)).getStatusCode();
    }

    @ParameterizedTest
    @MethodSource("getCitizenErrorValues")
    void resourceNotFound_CitizenError_ReturnsTrue(ResponseEntity<Object> citizenStatus) {
        assertTrue(resourceNotFound_CitizenError_checkValues(citizenStatus, REQUEST_OK));
        assertTrue(resourceNotFound_CitizenError_checkValues(REQUEST_OK, citizenStatus));
    }

    private boolean resourceNotFound_CitizenError_checkValues(ResponseEntity<Object> citizenStatus, ResponseEntity<Object> partyStatus) {
        when(MOCK_CITIZEN.getStatusCode()).thenReturn(citizenStatus.getStatusCode());
        when(MOCK_PARTY.getStatusCode()).thenReturn(partyStatus.getStatusCode());

        return checkResourceAvailability(MOCK_CITIZEN, MOCK_PARTY);
    }

    @Test
    void has_InValid_Municipality_Id_Return_Valid_Id_Response() {
        var mockAddress = mock(CitizenAddress.class);
        when(mockAddress.getMunicipality()).thenReturn("1234");
        when(mockAddress.getAddressType()).thenReturn("POPULATION_REGISTRATION_ADDRESS");

        var mockCitizen = mock(CitizenExtended.class);
        when(mockCitizen.getAddresses()).thenReturn(List.of(mockAddress));

        var mockResponseEntity = mock(ResponseEntity.class);
        when(mockResponseEntity.getBody()).thenReturn(mockCitizen);

        assertTrue(containsValidMunicipalityId(mockResponseEntity, "1234"));

        verify(mockAddress, times(1)).getMunicipality();
        verify(mockAddress, times(1)).getAddressType();
        verify(mockCitizen, times(2)).getAddresses();
        verify(mockResponseEntity, times(1)).getBody();
    }

    //This testCase should realistically never happen, but it is here for completeness.
    @Test
    void generateAssetTypeResponses_returnResponseOk_whenAssetTypeExists() {
        when(MOCK_ASSET.getType()).thenReturn(WANTED_ASSET_TYPE_NAME);
        when(MOCK_PARTY.getBody()).thenReturn(List.of(MOCK_ASSET));

        when(MOCK_PARTY.getBody()).thenReturn(List.of(MOCK_ASSET));

        ResponseEntity<List<PreCheckResponse>> result = generateAssetTypeResponses("", MOCK_PARTY);

        assertFalse(result.getBody().isEmpty());
        assertTrue(result.getBody().get(0).getAssetType().isEmpty());
        assertFalse(result.getBody().get(0).isEligible());
        assertFalse(result.getBody().get(0).getMessage().isEmpty());

        verify(MOCK_PARTY, times(0)).getBody();
    }

    @Test
    void generateAssetTypeResponses_returnResponseOk_whenAssetTypeDoesNotExist() {
        when(MOCK_ASSET.getType()).thenReturn("RandomAssetTypeName");
        when(MOCK_PARTY.getBody()).thenReturn(List.of(MOCK_ASSET));

        ResponseEntity<List<PreCheckResponse>> result = generateAssetTypeResponses(WANTED_ASSET_TYPE_NAME, MOCK_PARTY);

        assertFalse(result.getBody().isEmpty());
        assertEquals(1, result.getBody().size());
        assertFalse(result.getBody().get(0).getAssetType().isEmpty());
        assertTrue(result.getBody().get(0).isEligible());
        assertTrue(result.getBody().get(0).getMessage().isEmpty());

        verify(MOCK_PARTY, times(3)).getBody();
    }

    @ParameterizedTest
    @MethodSource("getAssetTypeValues")
    void generateAssetTypeResponses_returnResponseOk_whenEmpty(String assetType) {
        var mockResponseEntity = mock(ResponseEntity.class);
        when(mockResponseEntity.getBody()).thenReturn(List.of());

        ResponseEntity<List<PreCheckResponse>> result = generateAssetTypeResponses(assetType, mockResponseEntity);
        assertFalse(result.getBody().isEmpty());
        assert (result.getBody().size() == 1);
        assertFalse(result.getBody().get(0).getAssetType().isEmpty());
        assertTrue(result.getBody().get(0).isEligible());
        assertTrue(result.getBody().get(0).getMessage().isEmpty());

    }

    @Test
    void generateAssetTypeResponses_returnResponseOk_whenNull() {
        var mockResponseEntity = mock(ResponseEntity.class);
        when(mockResponseEntity.getBody()).thenReturn(null);

        ResponseEntity<List<PreCheckResponse>> result = generateAssetTypeResponses(WANTED_ASSET_TYPE_NAME, mockResponseEntity);

        assertFalse(result.getBody().isEmpty());
        assertFalse(result.getBody().get(0).getAssetType().isEmpty());
        assertTrue(result.getBody().get(0).isEligible());
        assertTrue(result.getBody().get(0).getMessage().isEmpty());

        verify(mockResponseEntity, times(1)).getBody();
    }

    @ParameterizedTest
    @MethodSource("getAssetTypeValues")
    void handleNoGivenAssetType_ReturnListOfAssets(String assetType) {
        var mockAsset1 = mock(Asset.class);
        when(mockAsset1.getType()).thenReturn(assetType);

        var mockAsset2 = mock(Asset.class);
        when(mockAsset2.getType()).thenReturn("RandomAssetTypeName");

        var mockResponseEntity = mock(ResponseEntity.class);
        when(mockResponseEntity.getBody()).thenReturn(List.of(mockAsset1, mockAsset2));

        ResponseEntity<List<PreCheckResponse>> result = generateNoAssetTypeResponses(mockResponseEntity);

        assertFalse(result.getBody().isEmpty());
        for (var asset : result.getBody()) {
            assertFalse(asset.getAssetType().isEmpty());
            assertFalse(asset.isEligible());
            assertTrue(asset.getMessage().isEmpty());
        }

        verify(mockResponseEntity, times(1)).getBody();
    }

    @ParameterizedTest
    @MethodSource("getAssetTypeValues")
    void generateAssetTypeResponses_ReturnsResponseWhenAssetTypeExists(String assetType) {
        Asset assetType_asset = new Asset().type(assetType);

        var mockResponseEntity = ResponseEntity.of(Optional.of(List.of(assetType_asset)));
        ResponseEntity<List<PreCheckResponse>> result = generateAssetTypeResponses(WANTED_ASSET_TYPE_NAME, mockResponseEntity);

        assertFalse(result.getBody().isEmpty());
        assertFalse(result.getBody().get(0).getAssetType().isEmpty());

        if (assetType.equals(WANTED_ASSET_TYPE_NAME)) {
            assertFalse(result.getBody().get(0).isEligible());
            assertFalse(result.getBody().get(0).getMessage().isEmpty());
        } else {
            assertTrue(result.getBody().get(0).isEligible());
            assertTrue(result.getBody().get(0).getMessage().isEmpty());
        }

    }

    @Test
    void handleNoGivenAssetType_Null_ReturnEmpty() {
        var mockResponseEntity = mock(ResponseEntity.class);
        when(mockResponseEntity.getBody()).thenReturn(null);

        ResponseEntity<List<PreCheckResponse>> result = generateNoAssetTypeResponses(mockResponseEntity);

        assertTrue(result.getBody().isEmpty());
        verify(mockResponseEntity, times(1)).getBody();
    }

    @Test
    void handleNoGivenAssetType_Empty_ReturnEmpty() {
        var mockResponseEntity = mock(ResponseEntity.class);
        when(mockResponseEntity.getBody()).thenReturn(List.of());

        ResponseEntity<List<PreCheckResponse>> result = generateNoAssetTypeResponses(mockResponseEntity);

        assertTrue(result.getBody().isEmpty());
        verify(mockResponseEntity, times(1)).getBody();
    }

    @Test
    void buildPreCheckResponse_ReturnValidPreCheckResponse() {
        PreCheckResponse result = createPrecheckResponse("assetType", true, "");

        assertNotNull(result);
        assertFalse(result.getAssetType().isEmpty());
        assertTrue(result.isEligible());
        assertTrue(result.getMessage().isEmpty());

        verify(MOCK_ASSET, times(0)).getType();
    }
}