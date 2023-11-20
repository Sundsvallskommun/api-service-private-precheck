package se.sundsvall.precheck.integration.partyassets.configuration;

import generated.client.partyAssets.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import se.sundsvall.precheck.integration.partyassets.PartyAssetsClient;

import java.util.List;

public class PartyAssetsIntegration {
    public static final String INTEGRATION_NAME = "PartyAssets";
    private static final Logger LOGGER = LoggerFactory.getLogger(PartyAssetsIntegration.class);
    private final PartyAssetsClient PARTY_ASSETS_CLIENT;

    public PartyAssetsIntegration(final PartyAssetsClient partyAssets) {
        this.PARTY_ASSETS_CLIENT = partyAssets;
    }

    public ResponseEntity<List<Asset>> getPartyAssets(final String personId, final String status) {
        try {
            return PARTY_ASSETS_CLIENT.getPartyAssets(personId, status);
        } catch (Exception e) {
            LOGGER.info("Unable to get personId for person", e);

            return null;
        }
    }
}
