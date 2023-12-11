package se.sundsvall.precheck.integration.citizen;

import feign.FeignException;
import feign.Request;
import generated.client.citizen.CitizenExtended;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CitizenIntegrationTest {
    private static final String PARTY_ID = "123";
    private static final ResponseEntity<CitizenExtended> NOT_FOUND_RESPONSE = ResponseEntity.notFound().build();
    private static final ResponseEntity<CitizenExtended> UNAUTHORIZED_RESPONSE = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    private static final ResponseEntity<CitizenExtended> INTERNAL_SERVER_ERROR_RESPONSE = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    private final ResponseEntity<CitizenExtended> SUCCESS_RESPONSE = ResponseEntity.ok(new CitizenExtended());
    @Mock
    private CitizenClient client;
    @InjectMocks
    private CitizenIntegration integration;

    private static Stream<ResponseEntity<CitizenExtended>> responseProvider() {
        return Stream.of(
                NOT_FOUND_RESPONSE,
                INTERNAL_SERVER_ERROR_RESPONSE,
                UNAUTHORIZED_RESPONSE
        );
    }

    @Test
    void getCitizen_shouldOk() {
        when(client.getCitizen(PARTY_ID)).thenReturn(SUCCESS_RESPONSE);

        ResponseEntity<CitizenExtended> actualResponse = integration.getCitizen(PARTY_ID);
        assertNotNull(actualResponse);
        assertEquals(actualResponse, SUCCESS_RESPONSE);
    }

    @ParameterizedTest
    @MethodSource("responseProvider")
    void getCitizen_shouldReturnExpectedResponse(ResponseEntity<CitizenExtended> expectedResponse) {
        when(client.getCitizen(PARTY_ID)).thenReturn(expectedResponse);

        ResponseEntity<CitizenExtended> actualResponse = integration.getCitizen(PARTY_ID);

        assertNotNull(actualResponse);
        assertEquals(actualResponse.getStatusCode(), expectedResponse.getStatusCode());
    }

    @Test
    void getCitizen_shouldReturnUnauthorizedResponse_whenClientThrowsClientAuthorizationException() {
        final OAuth2Error error = new OAuth2Error("unauthorized", "Unauthorized access to the resource", null);

        when(client.getCitizen(PARTY_ID)).thenThrow(new ClientAuthorizationException(error, "citizen", "Unauthorized"));

        final ResponseEntity<CitizenExtended> actualResponse = integration.getCitizen(PARTY_ID);

        assertNotNull(actualResponse);
        assertEquals(UNAUTHORIZED_RESPONSE, actualResponse);
    }

    @Test
    void getPartyAssets_shouldReturnInternalServerErrorResponse_whenClientThrowsFeignException() {
        Request request = Request.create(Request.HttpMethod.GET, "http://localhost:8080/assets?partyId=123&status=active", Collections.emptyMap(), Request.Body.empty().asBytes(), null);


        Map<String, Collection<String>> headers = new HashMap<>();
        headers.put("Content-Type", Collections.singletonList("application/json"));

        when(client.getCitizen(PARTY_ID)).thenThrow(new FeignException.InternalServerError("Internal Server Error", request, null, headers));
        final ResponseEntity<CitizenExtended> actualResponse = integration.getCitizen(PARTY_ID);

        assertEquals(INTERNAL_SERVER_ERROR_RESPONSE.getStatusCode(), actualResponse.getStatusCode());
    }

}