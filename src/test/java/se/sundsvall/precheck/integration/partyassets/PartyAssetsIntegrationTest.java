package se.sundsvall.precheck.integration.partyassets;


import feign.FeignException;
import feign.Request;
import generated.client.partyAssets.Asset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import se.sundsvall.precheck.api.model.Status;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartyAssetsIntegrationTest {
    private static final String PARTY_ID = "123";
    private static final ResponseEntity<List<Asset>> UNAUTHORIZED_RESPONSE = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    private static final ResponseEntity<List<Asset>> INTERNAL_SERVER_ERROR_RESPONSE = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    private static final ResponseEntity<List<Asset>> SUCCESS_RESPONSE = ResponseEntity.ok(List.of(new Asset()));
    @MockBean
    private PartyAssetsClient CLIENT;
    @InjectMocks
    private PartyAssetsIntegration INTEGRATION;

    @BeforeEach
    void setUp() {
        CLIENT = mock(PartyAssetsClient.class);
        INTEGRATION = new PartyAssetsIntegration(CLIENT);
    }

    @Test
    void getPartyAssets_shouldOk() {
        when(CLIENT.getPartyAssets(PARTY_ID, Status.ACTIVE.toString())).thenReturn(SUCCESS_RESPONSE);

        ResponseEntity<List<Asset>> actualResponse = INTEGRATION.getPartyAssets(PARTY_ID, Status.ACTIVE);
        assertNotNull(actualResponse);
        assertEquals(SUCCESS_RESPONSE, actualResponse);
    }

    @ParameterizedTest
    @EnumSource(value = HttpStatus.class, names = {"NOT_FOUND", "INTERNAL_SERVER_ERROR", "UNAUTHORIZED"})
    void getPartyAssets_InvalidPartyId_ReturnsErrorResponse(HttpStatus status) {
        ResponseEntity<List<Asset>> expectedResponse = ResponseEntity.status(status).build();
        when(CLIENT.getPartyAssets(PARTY_ID, Status.ACTIVE.toString())).thenReturn(expectedResponse);

        ResponseEntity<List<Asset>> actualResponse = INTEGRATION.getPartyAssets(PARTY_ID, Status.ACTIVE);

        assertAll(
                () -> assertNotNull(actualResponse),
                () -> assertEquals(actualResponse.getStatusCode(), expectedResponse.getStatusCode())
        );
    }

    //Here for code coverage completeness
    @Test
    void getPartyAssets_shouldReturnUnauthorizedResponse_whenClientThrowsClientAuthorizationException() {
        final OAuth2Error error = new OAuth2Error("unauthorized", "Unauthorized access to the resource", null);

        when(CLIENT.getPartyAssets(PARTY_ID, Status.ACTIVE.toString())).thenThrow(new ClientAuthorizationException(error, PartyAssetsIntegration.INTEGRATION_NAME, "Unauthorized"));

        final ResponseEntity<List<Asset>> actualResponse = INTEGRATION.getPartyAssets(PARTY_ID, Status.ACTIVE);

        assertNotNull(actualResponse);
        assertEquals(UNAUTHORIZED_RESPONSE, actualResponse);
    }

    @Test
    void getPartyAssets_shouldReturnInternalServerErrorResponse_whenClientThrowsFeignException() {
        Request request = Request.create(Request.HttpMethod.GET, "http://localhost:8080/assets?partyId=123&status=active", Collections.emptyMap(), Request.Body.empty().asBytes(), null);


        Map<String, Collection<String>> headers = new HashMap<>();
        headers.put("Content-Type", Collections.singletonList("application/json"));

        when(CLIENT.getPartyAssets(PARTY_ID, Status.ACTIVE.toString())).thenThrow(new FeignException.InternalServerError("Internal Server Error", request, null, headers));
        final ResponseEntity<List<Asset>> actualResponse = INTEGRATION.getPartyAssets(PARTY_ID, Status.ACTIVE);

        assertEquals(INTERNAL_SERVER_ERROR_RESPONSE.getStatusCode(), actualResponse.getStatusCode());
    }

    @Test
    void testGetPartyAssetsOtherException() {
        String a = "some value";
        RuntimeException runtimeException = new RuntimeException("some message");
        ResponseEntity<List<Asset>> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(CLIENT.getPartyAssets(a, Status.ACTIVE.toString())).thenThrow(runtimeException);

        ResponseEntity<List<Asset>> actual = INTEGRATION.getPartyAssets(a, Status.ACTIVE);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}