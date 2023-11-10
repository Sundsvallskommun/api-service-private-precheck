package se.sundsvall.precheck.integration.partyAssets.configuration;

import generated.client.partyAssets.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import se.sundsvall.precheck.integration.partyAssets.PartyAssetsClient;

import java.util.List;

public class PartyAssetsIntegration {
    public static final String INTEGRATION_NAME = "PartyAssets";
    private static final Logger LOG = LoggerFactory.getLogger(PartyAssetsIntegration.class);
    private final PartyAssetsClient client;

    public PartyAssetsIntegration(final PartyAssetsClient party) {
        this.client = party;
    }

    public ResponseEntity<List<Asset>> getPartyAssets(final String personId, final String status) {
        try {
            return client.getPartyAssets(personId, status);
        } catch (Exception e) {
            LOG.info("Unable to get personId for person", e);

            return null;
        }
    }
}
