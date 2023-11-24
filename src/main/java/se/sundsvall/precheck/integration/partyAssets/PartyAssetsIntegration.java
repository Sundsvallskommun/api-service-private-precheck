package se.sundsvall.precheck.integration.partyAssets;

import generated.client.partyAssets.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import se.sundsvall.precheck.api.model.Status;

import java.util.List;

@Component
public class PartyAssetsIntegration {

    static final String INTEGRATION_NAME = "party-assets";

    private static final Logger LOGGER = LoggerFactory.getLogger(PartyAssetsIntegration.class);

    private final PartyAssetsClient client;

    public PartyAssetsIntegration(final PartyAssetsClient client) {
        this.client = client;
    }

    public ResponseEntity<List<Asset>> getPartyAssets(final String a, final Status status) {
        try {
            LOGGER.info("Calling PartyAssets service");
            return client.getPartyAssets(a, status.toString());
        } catch (Exception e) {
            LOGGER.error("Error when calling PartyAssets service: ", e);
            return ResponseEntity.notFound().build();
        }
    }

}
