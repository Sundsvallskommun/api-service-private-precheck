package se.sundsvall.precheck.integration.partyassets;


import feign.FeignException;
import feign.Request;
import generated.client.partyAssets.Asset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.sundsvall.precheck.api.model.Status;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class PartyAssetsIntegrationTest {
    private static final String PARTY_ID = "123";
    private static final Status STATUS = Status.ACTIVE;
    private static final ResponseEntity<List<Asset>> NOT_FOUND_RESPONSE = ResponseEntity.notFound().build();
    private static final ResponseEntity<List<Asset>> UNAUTHORIZED_RESPONSE = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    private static final ResponseEntity<List<Asset>> INTERNAL_SERVER_ERROR_RESPONSE = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    private final ResponseEntity<List<Asset>> SUCCESS_RESPONSE;
    @Mock
    private PartyAssetsClient client;
    @InjectMocks
    private PartyAssetsIntegration integration;

    public PartyAssetsIntegrationTest() {
        final Asset first_asset = new Asset();
        first_asset.setAssetId("1");
        first_asset.setType("first asset");

        final Asset second_asset = new Asset();
        second_asset.setAssetId("2");
        second_asset.setType("second asset");

        SUCCESS_RESPONSE = ResponseEntity.ok(List.of(first_asset, second_asset));
    }

    private static Stream<ResponseEntity<List<Asset>>> responseProvider() {
        return Stream.of(
                NOT_FOUND_RESPONSE,
                INTERNAL_SERVER_ERROR_RESPONSE,
                UNAUTHORIZED_RESPONSE
        );
    }

    @Test
    void getPartyAssets_shouldOk() {
        when(client.getPartyAssets(PARTY_ID, STATUS.toString())).thenReturn(SUCCESS_RESPONSE);

        ResponseEntity<List<Asset>> actualResponse = integration.getPartyAssets(PARTY_ID, STATUS);
        assertNotNull(actualResponse);
        assertEquals(actualResponse, SUCCESS_RESPONSE);
    }

    @ParameterizedTest
    @MethodSource("responseProvider")
    void getPartyAssets_shouldReturnExpectedResponse(ResponseEntity<List<Asset>> expectedResponse) {
        when(client.getPartyAssets(PARTY_ID, STATUS.toString())).thenReturn(expectedResponse);

        ResponseEntity<List<Asset>> actualResponse = integration.getPartyAssets(PARTY_ID, STATUS);

        assertNotNull(actualResponse);
        assertEquals(actualResponse.getStatusCode(), expectedResponse.getStatusCode());
    }

    @Test
    void getPartyAssets_shouldReturnUnauthorizedResponse_whenClientThrowsClientAuthorizationException() {
        final OAuth2Error error = new OAuth2Error("unauthorized", "Unauthorized access to the resource", null);

        when(client.getPartyAssets(PARTY_ID, STATUS.toString())).thenThrow(new ClientAuthorizationException(error, PartyAssetsIntegration.INTEGRATION_NAME, "Unauthorized"));

        final ResponseEntity<List<Asset>> actualResponse = integration.getPartyAssets(PARTY_ID, STATUS);

        assertNotNull(actualResponse);
        assertEquals(UNAUTHORIZED_RESPONSE, actualResponse);
    }


    @Test
    void getPartyAssets_shouldReturnInternalServerErrorResponse_whenClientThrowsFeignException() {
        Request request = Request.create(Request.HttpMethod.GET, "http://localhost:8080/assets?partyId=123&status=active", Collections.emptyMap(), Request.Body.empty().asBytes(), null);


        Map<String, Collection<String>> headers = new HashMap<>();
        headers.put("Content-Type", Collections.singletonList("application/json"));

        when(client.getPartyAssets(PARTY_ID, STATUS.toString())).thenThrow(new FeignException.InternalServerError("Internal Server Error", request, null, headers));
        final ResponseEntity<List<Asset>> actualResponse = integration.getPartyAssets(PARTY_ID, STATUS);

        assertEquals(INTERNAL_SERVER_ERROR_RESPONSE.getStatusCode(), actualResponse.getStatusCode());
    }
}