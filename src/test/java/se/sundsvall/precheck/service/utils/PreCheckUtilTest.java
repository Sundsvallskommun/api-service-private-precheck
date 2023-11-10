package se.sundsvall.precheck.service.utils;

import generated.client.citizen.CitizenAddress;
import generated.client.citizen.CitizenExtended;
import generated.client.partyAssets.Asset;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import se.sundsvall.precheck.api.model.PreCheckResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static se.sundsvall.precheck.service.utils.PreCheckUtil.*;

class PreCheckUtilTest {
    private final ResponseEntity<CitizenExtended> citizen = mock(ResponseEntity.class);
    private final ResponseEntity<List<Asset>> party = mock(ResponseEntity.class);

    @Test
    void resourceNotFound_RerunsFalse_ValidInput() {
        when(citizen.getStatusCode()).thenReturn(HttpStatus.OK);
        when(party.getStatusCode()).thenReturn(HttpStatus.OK);

        boolean result = resourceNotFound(citizen, party);

        assertFalse(result);

        verify(citizen, times(2)).getStatusCode();
        verify(party, times(2)).getStatusCode();
    }

    @Test
    void resourceNotFound_CitizenError_ReturnsTrue() {
        //Status codes that can be returned from the citizen service call
        final ResponseEntity<Object> REQUEST_OK = ResponseEntity.ok().build();
        final ResponseEntity<Object> REQUEST_ERROR_INTERNAL_ERROR = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        final ResponseEntity<Object> REQUEST_ERROR_BAD_REQUEST = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        final ResponseEntity<Object> REQUEST_ERROR_NO_CONTENT = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        final ResponseEntity<Object> REQUEST_ERROR_FORBIDDEN = ResponseEntity.status(HttpStatus.FORBIDDEN).build();


        assertTrue(resourceNotFound_CitizenError_checkValues(REQUEST_ERROR_INTERNAL_ERROR, REQUEST_OK));
        assertTrue(resourceNotFound_CitizenError_checkValues(REQUEST_ERROR_BAD_REQUEST, REQUEST_OK));
        assertTrue(resourceNotFound_CitizenError_checkValues(REQUEST_ERROR_NO_CONTENT, REQUEST_OK));
        assertTrue(resourceNotFound_CitizenError_checkValues(REQUEST_ERROR_FORBIDDEN, REQUEST_OK));


        assertTrue(resourceNotFound_CitizenError_checkValues(REQUEST_OK, REQUEST_ERROR_INTERNAL_ERROR));
        assertTrue(resourceNotFound_CitizenError_checkValues(REQUEST_OK, REQUEST_ERROR_BAD_REQUEST));
        assertTrue(resourceNotFound_CitizenError_checkValues(REQUEST_OK, REQUEST_ERROR_NO_CONTENT));
        assertTrue(resourceNotFound_CitizenError_checkValues(REQUEST_OK, REQUEST_ERROR_FORBIDDEN));
    }

    private boolean resourceNotFound_CitizenError_checkValues(ResponseEntity<Object> citizenStatus, ResponseEntity<Object> partyStatus) {
        when(citizen.getStatusCode()).thenReturn(citizenStatus.getStatusCode());
        when(party.getStatusCode()).thenReturn(partyStatus.getStatusCode());

        return resourceNotFound(citizen, party);
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


        assertFalse(hasInValidMunicipalityId(mockResponseEntity, "1234"));


        verify(mockAddress, times(1)).getMunicipality();
        verify(mockAddress, times(1)).getAddressType();
        verify(mockCitizen, times(2)).getAddresses();
        verify(mockResponseEntity, times(1)).getBody();
    }
    @Test
    void handleAssetType_returnResponseOk() {
        var mockAsset = mock(Asset.class);
        when(mockAsset.getType()).thenReturn("NotOwnedAssetType");

        var mockResponseEntity = mock(ResponseEntity.class);
        when(mockResponseEntity.getBody()).thenReturn(List.of(mockAsset));

        ResponseEntity<List<PreCheckResponse>> result = handleAssetType("WantedAssetTypeName", mockResponseEntity);

        assertFalse(result.getBody().isEmpty());
        assertFalse(result.getBody().get(0).getAssetType().isEmpty());
        assertTrue(result.getBody().get(0).isEligible());
        assertTrue(result.getBody().get(0).getMessage().isEmpty());

        verify(mockResponseEntity, times(1)).getBody();
        verify(mockAsset, times(1)).getType();
    }

    @Test
    void handleAssetType_returnResponseOk_whenEmpty() {
        var mockResponseEntity = mock(ResponseEntity.class);
        when(mockResponseEntity.getBody()).thenReturn(List.of());

        ResponseEntity<List<PreCheckResponse>> result = handleAssetType("WantedAssetTypeName", mockResponseEntity);

        assertFalse(result.getBody().isEmpty());
        assertFalse(result.getBody().get(0).getAssetType().isEmpty());
        assertTrue(result.getBody().get(0).isEligible());
        assertFalse(result.getBody().get(0).getMessage().isEmpty());

        verify(mockResponseEntity, times(1)).getBody();
        verify(mockResponseEntity, times(1)).getBody();
    }

    @Test
    void handleAssetType_returnResponseOk_whenNull() {
        var mockResponseEntity = mock(ResponseEntity.class);
        when(mockResponseEntity.getBody()).thenReturn(null);

        ResponseEntity<List<PreCheckResponse>> result = handleAssetType("WantedAssetTypeName", mockResponseEntity);

        assertFalse(result.getBody().isEmpty());
        assertFalse(result.getBody().get(0).getAssetType().isEmpty());
        assertFalse(result.getBody().get(0).isEligible());
        assertFalse(result.getBody().get(0).getMessage().isEmpty());

        verify(mockResponseEntity, times(1)).getBody();
    }

    @Test
    void handleAssetType_returnResponseOk_whenAssetTypeExists() {
        var mockAsset = mock(Asset.class);
        when(mockAsset.getType()).thenReturn("WantedAssetTypeName");

        var mockResponseEntity = mock(ResponseEntity.class);
        when(mockResponseEntity.getBody()).thenReturn(List.of(mockAsset));


        ResponseEntity<List<PreCheckResponse>> result = handleAssetType("WantedAssetTypeName", mockResponseEntity);

        assertFalse(result.getBody().isEmpty());
        assertFalse(result.getBody().get(0).getAssetType().isEmpty());
        assertFalse(result.getBody().get(0).isEligible());
        assertFalse(result.getBody().get(0).getMessage().isEmpty());

        verify(mockResponseEntity, times(1)).getBody();
        verify(mockAsset, times(1)).getType();
    }

    @Test
    void handleAssetType_returnResponseOk_whenAssetTypeExists_multipleAssetTypes() {
        var mockAsset1 = mock(Asset.class);
        when(mockAsset1.getType()).thenReturn("WantedAssetTypeName");

        var mockAsset2 = mock(Asset.class);
        when(mockAsset2.getType()).thenReturn("NotWantedAssetTypeName");

        var mockResponseEntity = mock(ResponseEntity.class);
        when(mockResponseEntity.getBody()).thenReturn(List.of(mockAsset1, mockAsset2));

        ResponseEntity<List<PreCheckResponse>> result = handleAssetType("WantedAssetTypeName", mockResponseEntity);

        assertFalse(result.getBody().isEmpty());
        assertFalse(result.getBody().get(0).getAssetType().isEmpty());
        assertFalse(result.getBody().get(0).isEligible());
        assertFalse(result.getBody().get(0).getMessage().isEmpty());
    }

    @Test
    void handleNoGivenAssetType_ReturnListOfAssets() {
        var mockAsset1 = mock(Asset.class);
        when(mockAsset1.getType()).thenReturn("WantedAssetTypeName");

        var mockAsset2 = mock(Asset.class);
        when(mockAsset2.getType()).thenReturn("NotWantedAssetTypeName");

        var mockResponseEntity = mock(ResponseEntity.class);
        when(mockResponseEntity.getBody()).thenReturn(List.of(mockAsset1, mockAsset2));

        ResponseEntity<List<PreCheckResponse>> result = handleNoGivenAssetType(mockResponseEntity);


        assertFalse(result.getBody().isEmpty());
        for (var asset : result.getBody()) {
            assertFalse(asset.getAssetType().isEmpty());
            assertFalse(asset.isEligible());
            assertTrue(asset.getMessage().isEmpty());
        }

        verify(mockResponseEntity, times(1)).getBody();
    }

    @Test
    void handleNoGivenAssetType_Null_ReturnEmpty() {
        var mockResponseEntity = mock(ResponseEntity.class);
        when(mockResponseEntity.getBody()).thenReturn(null);

        ResponseEntity<List<PreCheckResponse>> result = handleNoGivenAssetType(mockResponseEntity);

        assertTrue(result.getBody().isEmpty());
        verify(mockResponseEntity, times(1)).getBody();
    }

    @Test
    void handleNoGivenAssetType_Empty_ReturnEmpty() {
        var mockResponseEntity = mock(ResponseEntity.class);
        when(mockResponseEntity.getBody()).thenReturn(List.of());

        ResponseEntity<List<PreCheckResponse>> result = handleNoGivenAssetType(mockResponseEntity);

        assertTrue(result.getBody().isEmpty());
        verify(mockResponseEntity, times(1)).getBody();
    }

    @Test
    void buildPreCheckResponse_ReturnValidPreCheckResponse() {
        PreCheckResponse result = buildPrecheckResponse("assetType", true, "");

        assertNotNull(result);
        assertFalse(result.getAssetType().isEmpty());
        assertTrue(result.isEligible());
        assertTrue(result.getMessage().isEmpty());
    }

}
