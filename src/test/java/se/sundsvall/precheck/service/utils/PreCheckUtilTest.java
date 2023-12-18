package se.sundsvall.precheck.service.utils;

import generated.client.citizen.CitizenAddress;
import generated.client.citizen.CitizenExtended;
import generated.client.partyAssets.Asset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import se.sundsvall.precheck.api.model.PreCheckResponse;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.precheck.service.utils.PreCheckUtil.containsValidMunicipalityId;
import static se.sundsvall.precheck.service.utils.PreCheckUtil.generateAssetTypeResponses;

class PreCheckUtilTest {
    private static final String WANTED_ASSET_TYPE_NAME = "WantedAssetTypeName";
    private ResponseEntity<CitizenExtended> MOCK_CITIZEN_RESPONSE_ENTITY;

    @Mock
    private ResponseEntity<List<Asset>> mockPartyResponseEntity;

    @Mock
    private List<Asset> mock_party_response_entity_body = mock(List.class);
    @Mock
    private Asset MOCK_ASSET = mock(Asset.class);


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

    @BeforeEach
    void setUp() {
        MOCK_CITIZEN_RESPONSE_ENTITY = mock(ResponseEntity.class);
        mockPartyResponseEntity = mock(ResponseEntity.class);
        MOCK_ASSET = mock(Asset.class);


    }

    @Test
    void tryInstantiatePreCheckUtil_ThrowsIllegalAccessException() {
        assertThrows(InvocationTargetException.class, () -> {
            Constructor<PreCheckUtil> constructor = PreCheckUtil.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
    }


    @Test
    void checkResourceAvailability_WhenCitizenAndPartyAreValid_ReturnsFalse() {
        when(MOCK_CITIZEN_RESPONSE_ENTITY.getStatusCode()).thenReturn(HttpStatus.OK);
        when(mockPartyResponseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(MOCK_CITIZEN_RESPONSE_ENTITY.getBody()).thenReturn(new CitizenExtended());

        boolean result = PreCheckUtil.checkResourceAvailability(MOCK_CITIZEN_RESPONSE_ENTITY, mockPartyResponseEntity);

        assertFalse(result);
    }

    @Test
    void checkResourceAvailability_WhenNullCitizenAndParty_ReturnsTrue() {
        boolean result = PreCheckUtil.checkResourceAvailability(null, null);

        assertTrue(result);
    }

    @Test
    void checkResourceAvailability_WhenNullCitizenBodyIsNull_ReturnsTrue() {
        when(MOCK_CITIZEN_RESPONSE_ENTITY.getStatusCode()).thenReturn(HttpStatus.OK);
        when(mockPartyResponseEntity.getStatusCode()).thenReturn(HttpStatus.OK);

        boolean result = PreCheckUtil.checkResourceAvailability(MOCK_CITIZEN_RESPONSE_ENTITY, mockPartyResponseEntity);

        assertTrue(result);
    }

    @ParameterizedTest
    @MethodSource("getCitizenErrorValues")
    void checkResourceAvailability_resourceNotFound_CitizenErrors_ReturnsTrue(ResponseEntity<Object> citizenStatus) {
        when(MOCK_CITIZEN_RESPONSE_ENTITY.getStatusCode()).thenReturn(citizenStatus.getStatusCode());
        when(mockPartyResponseEntity.getStatusCode()).thenReturn(HttpStatus.OK);

        boolean result = PreCheckUtil.checkResourceAvailability(MOCK_CITIZEN_RESPONSE_ENTITY, mockPartyResponseEntity);

        assertTrue(result);
    }

    @ParameterizedTest
    @MethodSource("getCitizenErrorValues")
    void checkResourceAvailability_resourceNotFound_CitizenError_ReturnsTrue(ResponseEntity<Object> citizenStatus) {
        when(MOCK_CITIZEN_RESPONSE_ENTITY.getStatusCode()).thenReturn(citizenStatus.getStatusCode());

        assertTrue(PreCheckUtil.checkResourceAvailability(MOCK_CITIZEN_RESPONSE_ENTITY, mockPartyResponseEntity));
    }

    @Test
    void containsValidMunicipalityId_WithValidCitizenAndMunicipalityId_ReturnsTrue() {
        var mockAddress = mock(CitizenAddress.class);
        var mockCitizen = mock(CitizenExtended.class);

        when(mockAddress.getMunicipality()).thenReturn("1234");
        when(mockAddress.getAddressType()).thenReturn("POPULATION_REGISTRATION_ADDRESS");

        when(mockCitizen.getAddresses()).thenReturn(Collections.singletonList(mockAddress));
        when(MOCK_CITIZEN_RESPONSE_ENTITY.getBody()).thenReturn(mockCitizen);

        boolean result = PreCheckUtil.containsValidMunicipalityId(MOCK_CITIZEN_RESPONSE_ENTITY, "1234");

        assertTrue(result);
    }

    @Test
    void containsValidMunicipalityId_WithNullCitizenEntity_ReturnsFalse() {
        boolean result = PreCheckUtil.containsValidMunicipalityId(null, "municipalityId");

        assertFalse(result);
    }

    @Test
    void containsValidMunicipalityId_WithNullMunicipalityId_ReturnsFalse() {
        CitizenExtended mockCitizen = new CitizenExtended();
        when(MOCK_CITIZEN_RESPONSE_ENTITY.getBody()).thenReturn(mockCitizen);

        boolean result = PreCheckUtil.containsValidMunicipalityId(MOCK_CITIZEN_RESPONSE_ENTITY, null);

        assertFalse(result);
    }

    @Test
    void generateAssetTypeResponses_ReturnsResponseWhenAssetTypeExists() {
        when(MOCK_ASSET.getType()).thenReturn("WantedAssetTypeName");
        when(mockPartyResponseEntity.getBody()).thenReturn(Collections.singletonList(MOCK_ASSET));

        List<PreCheckResponse> result = PreCheckUtil.generateAssetTypeResponses("WantedAssetTypeName", mockPartyResponseEntity);

        assertFalse(result.isEmpty());
        assertFalse(result.get(0).getAssetType().isEmpty());
        assertFalse(result.get(0).isEligible());
        assertFalse(result.get(0).getMessage().isEmpty());
    }

    @Test
    void generateAssetTypeResponses_ReturnsEmptyListWhenNull() {
        List<PreCheckResponse> result = PreCheckUtil.generateAssetTypeResponses(null, null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.get(0).getAssetType().isEmpty());
        assertFalse(result.get(0).isEligible());
        assertFalse(result.get(0).getMessage().isEmpty());
    }

    @Test
    void containsValidMunicipalityId_NullCitizenOrAddresses_ReturnsFalse() {
        CitizenExtended citizen = new CitizenExtended();
        citizen.setAddresses(null);
        assertFalse(containsValidMunicipalityId(ResponseEntity.ok(citizen), "municipalityId"));
    }


    @ParameterizedTest
    @MethodSource("getAssetTypeValues")
    void generateAssetTypeResponses_returnResponseOk_whenAssetTypeDoesNotExistInList(String assetType) {
        when(MOCK_ASSET.getType()).thenReturn("RandomAssetTypeName");
        when(mockPartyResponseEntity.getBody()).thenReturn(List.of(MOCK_ASSET));

        List<PreCheckResponse> result = generateAssetTypeResponses(assetType, mockPartyResponseEntity);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertFalse(result.get(0).getAssetType().isEmpty());
        assertTrue(result.get(0).isEligible());
        assertTrue(result.get(0).getMessage().isEmpty());
    }

    @ParameterizedTest
    @MethodSource("getAssetTypeValues")
    void generateAssetTypeResponses_returnResponseOk_whenAssetTypeEmpty(String assetType) {
        var mockResponseEntity = mock(ResponseEntity.class);
        when(mockResponseEntity.getBody()).thenReturn(List.of());

        List<PreCheckResponse> result = generateAssetTypeResponses(assetType, mockResponseEntity);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertFalse(result.get(0).getAssetType().isEmpty());
        assertTrue(result.get(0).isEligible());
        assertTrue(result.get(0).getMessage().isEmpty());
    }

    @Test
    void generateAssetTypeResponses_returnResponseOk_whenAssetTypeDoesNotExist() {
        when(mockPartyResponseEntity.getBody()).thenReturn(Collections.singletonList(mock(Asset.class)));

        List<PreCheckResponse> result = PreCheckUtil.generateAssetTypeResponses("NonExistingAssetType", mockPartyResponseEntity);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertFalse(result.get(0).getAssetType().isEmpty());
        assertTrue(result.get(0).isEligible());
        assertTrue(result.get(0).getMessage().isEmpty());
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

    @ParameterizedTest
    @MethodSource("getAssetTypeValues")
    void handleNoGivenAssetType_ReturnListOfAssets(String assetType) {
        Asset mockAsset1 = mock(Asset.class);
        when(mockAsset1.getType()).thenReturn(assetType);

        Asset mockAsset2 = mock(Asset.class);
        when(mockAsset2.getType()).thenReturn("RandomAssetTypeName");

        when(mockPartyResponseEntity.getBody()).thenReturn(List.of(mockAsset1, mockAsset2));

        List<PreCheckResponse> result = PreCheckUtil.generateNoAssetTypeResponses(mockPartyResponseEntity);

        assertFalse(result.isEmpty());
        for (PreCheckResponse response : result) {
            assertFalse(response.getAssetType().isEmpty());
            assertFalse(response.isEligible());
            assertTrue(response.getMessage().isEmpty());
        }

        verify(mockPartyResponseEntity, times(1)).getBody();
    }

    @Test
    void handleNoGivenAssetType_ReturnEmptyList() {
        when(mockPartyResponseEntity.getBody()).thenReturn(Collections.emptyList());

        List<PreCheckResponse> result = PreCheckUtil.generateNoAssetTypeResponses(mockPartyResponseEntity);

        assertTrue(result.isEmpty());
    }

    @Test
    void generateNoAssetTypeResponses_ReturnINTERNAL_SERVER_ERROR() {
        when(mockPartyResponseEntity.getBody()).thenReturn(mock_party_response_entity_body);
        when(mock_party_response_entity_body.stream()).thenThrow(new RuntimeException());


        Exception e = assertThrows(
                RuntimeException.class,
                () -> PreCheckUtil.generateNoAssetTypeResponses(mockPartyResponseEntity)
        );

        var expectedResponse = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating responses");

        assertEquals(org.springframework.web.server.ResponseStatusException.class, e.getClass());
        assertEquals(expectedResponse.getMessage(), e.getMessage());


    }
}