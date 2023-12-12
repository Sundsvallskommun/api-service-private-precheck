package se.sundsvall.precheck.integration.citizen;

import feign.FeignException;
import generated.client.citizen.CitizenExtended;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.stereotype.Component;

@Component
public class CitizenIntegration {

    public static final String INTEGRATION_NAME = "citizen";

    private static final Logger LOGGER = LoggerFactory.getLogger(CitizenIntegration.class);

    private final CitizenClient client;

    public CitizenIntegration(final CitizenClient client) {
        this.client = client;
    }

    public ResponseEntity<CitizenExtended> getCitizen(final String a) {
        try {
            LOGGER.info("Calling citizen service");
            return client.getCitizen(a);
        } catch (FeignException e) {
            logError(e);
            return ResponseEntity.status(e.status()).build();
        } catch (ClientAuthorizationException e) {
            logError(e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            logError(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void logError(Exception exception) {
        LOGGER.error("Error when calling getCitizen service", exception);
    }
}
