package se.sundsvall.precheck.service.utils;

import generated.client.citizen.CitizenAddress;
import generated.client.citizen.CitizenExtended;
import generated.client.partyAssets.Asset;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import se.sundsvall.precheck.api.model.PreCheckResponse;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.precheck.service.utils.PreCheckUtil.checkResourceAvailability;
import static se.sundsvall.precheck.service.utils.PreCheckUtil.containsValidMunicipalityId;
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
    void tryInstantiatePreCheckUtil_ThrowsIllegalAccessException() throws NoSuchMethodException {
        Constructor<PreCheckUtil> constructor = PreCheckUtil.class.getDeclaredConstructor();
        assertEquals(Modifier.PRIVATE, constructor.getModifiers());
        constructor.setAccessible(true);
        assertThrows(InvocationTargetException.class, constructor::newInstance);
    }

    @Test
    void resourceNotFound_RerunsFalse_InValid() {
        when(MOCK_CITIZEN.getStatusCode()).thenReturn(HttpStatus.OK);
        when(MOCK_PARTY.getStatusCode()).thenReturn(HttpStatus.OK);

        assertTrue(checkResourceAvailability(MOCK_CITIZEN, MOCK_PARTY));
        assertTrue(checkResourceAvailability(null, null));
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

        Assertions.assertDoesNotThrow(() -> checkResourceAvailability(MOCK_CITIZEN, MOCK_PARTY));
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
    }

    @Test
    void containsValidMunicipalityId_NullCitizenEntity_ReturnsFalse() {
        assertFalse(containsValidMunicipalityId(null, "municipalityId"));
        assertFalse(containsValidMunicipalityId(ResponseEntity.ok(null), "municipalityId"));
    }

    @Test
    void containsValidMunicipalityId_NullMunicipalityId_ReturnsFalse() {
        assertFalse(containsValidMunicipalityId(ResponseEntity.ok(new CitizenExtended()), null));
        assertFalse(containsValidMunicipalityId(ResponseEntity.ok(new CitizenExtended()), ""));
    }

    @Test
    void containsValidMunicipalityId_NullCitizenOrAddresses_ReturnsFalse() {
        CitizenExtended citizen = new CitizenExtended();
        citizen.setAddresses(null);
        assertFalse(containsValidMunicipalityId(ResponseEntity.ok(citizen), "municipalityId"));
    }


    @ParameterizedTest
    @MethodSource("getAssetTypeValues")
    void generateAssetTypeResponses_returnResponseOk_whenAssetTypeDoesNotExist(String assetType) {
        when(MOCK_ASSET.getType()).thenReturn("RandomAssetTypeName");
        when(MOCK_PARTY.getBody()).thenReturn(List.of(MOCK_ASSET));

        List<PreCheckResponse> result = generateAssetTypeResponses(assetType, MOCK_PARTY);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertFalse(result.get(0).getAssetType().isEmpty());
        assertTrue(result.get(0).isEligible());
        assertTrue(result.get(0).getMessage().isEmpty());
    }

    @ParameterizedTest
    @MethodSource("getAssetTypeValues")
    void generateAssetTypeResponses_returnResponseOk_whenEmpty(String assetType) {
        var mockResponseEntity = mock(ResponseEntity.class);
        when(mockResponseEntity.getBody()).thenReturn(List.of());

        List<PreCheckResponse> result = generateAssetTypeResponses(assetType, mockResponseEntity);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertFalse(result.get(0).getAssetType().isEmpty());
        assertTrue(result.get(0).isEligible());
        assertTrue(result.get(0).getMessage().isEmpty());
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

        List<PreCheckResponse> result = generateNoAssetTypeResponses(mockResponseEntity);

        assertFalse(result.isEmpty());
        for (var asset : result) {
            assertFalse(asset.getAssetType().isEmpty());
            assertFalse(asset.isEligible());
            assertTrue(asset.getMessage().isEmpty());
        }

        verify(mockResponseEntity, times(1)).getBody();
    }

    @Test
    void generateNoAssetTypeResponses_PartyAssetsNull_ReturnEmptList() {
        List<PreCheckResponse> result = generateNoAssetTypeResponses(null);

        assertEquals(Collections.emptyList(), result);
    }

    @ParameterizedTest
    @MethodSource("getAssetTypeValues")
    void generateAssetTypeResponses_ReturnsResponseWhenAssetTypeExists(String assetType) {
        Asset assetType_asset = new Asset().type(assetType);

        var mockResponseEntity = ResponseEntity.of(Optional.of(List.of(assetType_asset)));
        List<PreCheckResponse> result = generateAssetTypeResponses(WANTED_ASSET_TYPE_NAME, mockResponseEntity);

        assertFalse(result.isEmpty());
        assertFalse(result.get(0).getAssetType().isEmpty());

        if (assetType.equals(WANTED_ASSET_TYPE_NAME)) {
            assertFalse(result.get(0).isEligible());
            assertFalse(result.get(0).getMessage().isEmpty());
        } else {
            assertTrue(result.get(0).isEligible());
            assertTrue(result.get(0).getMessage().isEmpty());
        }
    }

    @Test
    void generateAssetTypeResponses_ReturnsEmptyListWhenNull() {
        List<PreCheckResponse> result = generateAssetTypeResponses(null, null);

        assertThat(result).isNotNull().satisfies(preCheckResponses -> {
            assertThat(preCheckResponses).isNotNull().isNotEmpty();
            assertThat(preCheckResponses.get(0).getAssetType()).isEmpty();
            assertThat(preCheckResponses.get(0).isEligible()).isFalse();
            assertThat(preCheckResponses.get(0).getMessage()).isNotEmpty();
        });
    }
}