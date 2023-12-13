package se.sundsvall.precheck.integration.citizen;

import feign.FeignException;
import feign.Request;
import generated.client.citizen.CitizenExtended;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import se.sundsvall.precheck.integration.partyassets.PartyAssetsIntegration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CitizenIntegrationTest {
    private static final String PARTY_ID = "123";
    private static final ResponseEntity<CitizenExtended> UNAUTHORIZED_RESPONSE = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    private static final ResponseEntity<CitizenExtended> INTERNAL_SERVER_ERROR_RESPONSE = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    private static final ResponseEntity<CitizenExtended> SUCCESS_RESPONSE = ResponseEntity.ok(new CitizenExtended());
    @MockBean
    private final CitizenClient CLIENT = mock(CitizenClient.class);
    @Autowired
    private final CitizenIntegration INTEGRATION = new CitizenIntegration(CLIENT);

    @Test
    void getPartyAssets_shouldOk() {
        when(CLIENT.getCitizen(PARTY_ID)).thenReturn(SUCCESS_RESPONSE);

        ResponseEntity<CitizenExtended> actualResponse = INTEGRATION.getCitizen(PARTY_ID);
        assertNotNull(actualResponse);
        assertEquals(SUCCESS_RESPONSE, actualResponse);
    }

    @ParameterizedTest
    @EnumSource(value = HttpStatus.class, names = {"NOT_FOUND", "INTERNAL_SERVER_ERROR", "UNAUTHORIZED"})
    void getPartyAssets_InvalidPartyId_ReturnsErrorResponse(HttpStatus status) {
        ResponseEntity<CitizenExtended> expectedResponse = ResponseEntity.status(status).build();
        when(CLIENT.getCitizen(PARTY_ID)).thenReturn(expectedResponse);

        ResponseEntity<CitizenExtended> actualResponse = INTEGRATION.getCitizen(PARTY_ID);

        assertAll(
                () -> assertNotNull(actualResponse),
                () -> assertEquals(actualResponse.getStatusCode(), expectedResponse.getStatusCode())
        );
    }

    //Here for code coverage completeness
    @Test
    void getPartyAssets_shouldReturnUnauthorizedResponse_whenClientThrowsClientAuthorizationException() {
        final OAuth2Error error = new OAuth2Error("unauthorized", "Unauthorized access to the resource", null);

        when(CLIENT.getCitizen(PARTY_ID)).thenThrow(new ClientAuthorizationException(error, PartyAssetsIntegration.INTEGRATION_NAME, "Unauthorized"));

        final ResponseEntity<CitizenExtended> actualResponse = INTEGRATION.getCitizen(PARTY_ID);

        assertNotNull(actualResponse);
        assertEquals(UNAUTHORIZED_RESPONSE, actualResponse);
    }

    @Test
    void getPartyAssets_shouldReturnInternalServerErrorResponse_whenClientThrowsFeignException() {
        Request request = Request.create(Request.HttpMethod.GET, "http://localhost:8080/assets?partyId=123&status=active", Collections.emptyMap(), Request.Body.empty().asBytes(), null);


        Map<String, Collection<String>> headers = new HashMap<>();
        headers.put("Content-Type", Collections.singletonList("application/json"));

        when(CLIENT.getCitizen(PARTY_ID)).thenThrow(new FeignException.InternalServerError("Internal Server Error", request, null, headers));
        final ResponseEntity<CitizenExtended> actualResponse = INTEGRATION.getCitizen(PARTY_ID);

        assertEquals(INTERNAL_SERVER_ERROR_RESPONSE.getStatusCode(), actualResponse.getStatusCode());
    }

    @Test
    void testGetPartyAssetsOtherException() {
        String a = "some value";
        RuntimeException runtimeException = new RuntimeException("some message");
        ResponseEntity<CitizenExtended> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(CLIENT.getCitizen(a)).thenThrow(runtimeException);

        ResponseEntity<CitizenExtended> actual = INTEGRATION.getCitizen(a);

        assertEquals(expected, actual);
    }
}