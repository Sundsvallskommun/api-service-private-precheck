package se.sundsvall.precheck.integration.citizen.configuration;

import generated.client.citizen.CitizenExtended;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import se.sundsvall.precheck.integration.citizen.CitizenClient;

@Component
public class CitizenIntegration {
    public static final String INTEGRATION_NAME = "Citizen";
    private static final Logger LOG = LoggerFactory.getLogger(CitizenIntegration.class);
    private final CitizenClient client;

    public CitizenIntegration(final CitizenClient client) {
        this.client = client;
    }

    public ResponseEntity<CitizenExtended> getCitizen(final String personId) {
        try {
            return client.getCitizen(personId);
        } catch (Exception e) {
            LOG.info("Unable to get personId for person", e);

            return null;
        }
    }
}
