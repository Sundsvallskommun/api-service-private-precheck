package se.sundsvall.precheck.integration.partyassets;

import feign.FeignException;
import generated.client.partyAssets.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.stereotype.Component;
import se.sundsvall.precheck.api.model.Status;

import java.util.List;

@Component
public class PartyAssetsIntegration {

    public static final String INTEGRATION_NAME = "party-assets";

    private static final Logger LOGGER = LoggerFactory.getLogger(PartyAssetsIntegration.class);

    private final PartyAssetsClient client;

    public PartyAssetsIntegration(final PartyAssetsClient client) {
        this.client = client;
    }

    public ResponseEntity<List<Asset>> getPartyAssets(final String a, final Status status) {
        try {
            LOGGER.info("Calling PartyAssets service");
            return client.getPartyAssets(a, status.toString());
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
        LOGGER.error("Error when calling getPartyAssets service", exception);
    }
}
